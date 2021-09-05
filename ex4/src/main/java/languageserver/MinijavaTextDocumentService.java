package languageserver;

import analysis.Analysis;
import frontend.SourcePosition;
import minijava.ast.MJElement;
import minijava.ast.MJVarDecl;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
public class MinijavaTextDocumentService implements TextDocumentService {

    private final MinijavaLanguageserver server;
    private final AnalysisServer analysisServer;

    public MinijavaTextDocumentService(MinijavaLanguageserver server) {
        this.server = server;
        this.analysisServer = server.getAnalysisServer();
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        analysisServer.checkDocument(params.getTextDocument());
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        analysisServer.checkVersionedDocument(params);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        server.log("did close " + params.getTextDocument().getUri());
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        server.log("did save " + params.getTextDocument().getUri());
    }

    @Override
    public CompletableFuture<Hover> hover(TextDocumentPositionParams position) {
        MJElement elem = findElement(position);
        if (elem == null) {
            return CompletableFuture.completedFuture(null);
        }

        String message = null;

        // TODO set the message based on what the element is

        if (message == null) {
            message = elem.getClass().getSimpleName();
        }
        Hover hover = new Hover(Collections.singletonList(Either.forLeft(message)), server.convertSource(elem.getSourcePosition()));
        return CompletableFuture.completedFuture(hover);
    }

    private MJElement findElement(TextDocumentPositionParams position) {
        TextDocumentIdentifier doc = position.getTextDocument();
        Analysis analysis = analysisServer.getAnalysisForFile(doc);
        if (analysis == null) {
            return null;
        }
        return findElement(analysis.getProg(), position.getPosition());
    }

    private MJElement findElement(MJElement elem, Position position) {
        // TODO find the right element here ...
        return null;
    }


    @Override
    public CompletableFuture<List<? extends Location>> definition(TextDocumentPositionParams position) {
        // TODO implement get definition
        return CompletableFuture.completedFuture(null);
    }
}
