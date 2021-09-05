package languageserver;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;

/**
 *
 */
public class MinijavaWorkspaceService implements WorkspaceService {

    private final MinijavaLanguageserver server;

    public MinijavaWorkspaceService(MinijavaLanguageserver server) {
        this.server = server;
    }


    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {

    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        server.log("shutdown");
    }
}
