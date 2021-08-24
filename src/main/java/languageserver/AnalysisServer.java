package languageserver;

import analysis.Analysis;
import frontend.MJFrontend;
import frontend.SyntaxError;
import minijava.ast.MJProgram;
import org.eclipse.lsp4j.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parses and analysis text documents
 */
public class AnalysisServer {
    private final MinijavaLanguageserver server;
    private final Map<String, FileAnalysis> analysisCache = new HashMap<>();


    public AnalysisServer(MinijavaLanguageserver server) {
        this.server = server;
    }

    public void checkDocument(TextDocumentItem textDocument) {
        synchronized (analysisCache) {
            String uri = textDocument.getUri();
            analysisCache.put(uri, new FileAnalysis(uri, 0, textDocument.getText()));
        }
    }

    public void checkVersionedDocument(DidChangeTextDocumentParams params) {
        synchronized (analysisCache) {
            VersionedTextDocumentIdentifier doc = params.getTextDocument();
            List<TextDocumentContentChangeEvent> changes = params.getContentChanges();
            if (changes.isEmpty()) {
                analysisCache.get(doc.getUri()).setVersion(doc.getVersion());

            } else {
                String text = changes.get(0).getText();
                analysisCache.put(doc.getUri(), new FileAnalysis(doc.getUri(), doc.getVersion(), text));
            }
        }
    }

    public Analysis getAnalysisForFile(TextDocumentIdentifier doc) {
        synchronized (analysisCache) {
            FileAnalysis fileAnalysis = analysisCache.get(doc.getUri());
            if (fileAnalysis == null) {
                return null;
            }
            return fileAnalysis.getAnalysis();
        }
    }

    public class FileAnalysis {

        private final String text;
        private final String uri;
        private final Analysis analysis;
        private int version;

        public FileAnalysis(String uri, int version, String text) {
            this.uri = uri;
            this.version = version;
            this.text = text;
            this.analysis = check();
        }

        private Analysis check() {
            MJFrontend frontend = new MJFrontend();
            MJProgram prog;
            try {
                prog = frontend.parseString(text);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (!frontend.getSyntaxErrors().isEmpty()) {
                reportErrors(frontend.getSyntaxErrors().stream());
            }
            if (prog == null) {
                return null;
            }

            Analysis analysis = new Analysis(prog);
            analysis.check();
            reportErrors(Stream.concat(
                    frontend.getSyntaxErrors().stream(),
                    analysis.getTypeErrors().stream()));
            return analysis;
        }

        private void reportErrors(Stream<SyntaxError> syntaxErrors) {
            server.reportError(uri, syntaxErrors
                    .map(e -> new Diagnostic(server.convertSource(e.getSource()), e.getMessage(), DiagnosticSeverity.Error, "minijava"))
                    .collect(Collectors.toList()));
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public Analysis getAnalysis() {
            return analysis;
        }
    }

}
