package languageserver;

import frontend.SourcePosition;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
public class MinijavaLanguageserver implements org.eclipse.lsp4j.services.LanguageServer, LanguageClientAware {


    private LanguageClient client;
    private AnalysisServer analysisServer = new AnalysisServer(this);

    /**
     * Starts the language server
     */
    public static void start() {
        MinijavaLanguageserver server = new MinijavaLanguageserver();
        Launcher<LanguageClient> launcher =
                LSPLauncher.createServerLauncher(server,
                        System.in,
                        System.out);
        // redirect all other output to StdErr, just to be sure:
        System.setOut(System.err);
        server.connect(launcher.getRemoteProxy());
        launcher.startListening();
    }

    public void log(String msg) {
        System.err.println(msg);
    }

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        ServerCapabilities capabilities = new ServerCapabilities();
        capabilities.setTextDocumentSync(Either.forLeft(TextDocumentSyncKind.Full));
        // Shows information when hovering the mouse over elements in the program
        // implementation in MinijavaTextDocumentService#hover
        capabilities.setHoverProvider(true);
        // Enables users to jump to the declaration of a name
        // implementation in MinijavaTextDocumentService#hover
        capabilities.setDefinitionProvider(true);
        // Examples of further interesting capabilities
//        capabilities.setCompletionProvider(new CompletionOptions(false, Collections.singletonList(".")));
//        capabilities.setSignatureHelpProvider(new SignatureHelpOptions(Arrays.asList("(", ".")));
//        capabilities.setDocumentHighlightProvider(true);
//        capabilities.setReferencesProvider(true);
//        capabilities.setRenameProvider(true);
//        capabilities.setDocumentSymbolProvider(true);


        InitializeResult res = new InitializeResult(capabilities);
        log("initialization done: " + params.getRootUri());
        return CompletableFuture.completedFuture(res);
    }

    @Override
    public void initialized(InitializedParams params) {
        log("initialized");
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        log("shutdown");
        return CompletableFuture.completedFuture("ok");
    }

    @Override
    public void exit() {
        log("exit");
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return new MinijavaTextDocumentService(this);
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return new MinijavaWorkspaceService(this);
    }

    public AnalysisServer getAnalysisServer() {
        return analysisServer;
    }

    public void reportError(String uri, List<Diagnostic> diagnostics) {
        client.publishDiagnostics(new PublishDiagnosticsParams(uri, diagnostics));
    }

    public Range convertSource(SourcePosition source) {
        return new Range(
                new Position(source.getLine() - 1, source.getColumn() - 1),
                new Position(source.getEndLine() - 1, source.getEndColumn() - 1)
        );
    }

}
