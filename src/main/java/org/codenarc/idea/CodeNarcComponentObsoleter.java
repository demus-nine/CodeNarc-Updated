package org.codenarc.idea;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginInstaller;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.PluginNode;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.updateSettings.impl.PluginDownloader;
import com.intellij.openapi.updateSettings.impl.UpdateChecker;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

//import com.intellij.ide.plugins.PluginManagerConfigurable;

public class CodeNarcComponentObsoleter implements BaseComponent {

    private static final String codenarcId = "CodeNarc";

    public void initComponent() {
        try {
            boolean willInstallCodenarclugin = false;
            boolean restart = false;
            IdeaPluginDescriptor codenarcDescriptor;
            PluginId codenarcPlugin = PluginId.getId(codenarcId);
            if (!PluginManager.isPluginInstalled(codenarcPlugin)) {
                codenarcDescriptor = new PluginNode(codenarcPlugin);
                PluginDownloader downloader = PluginDownloader.createDownloader(codenarcDescriptor);
                downloader.prepareToInstall(new EmptyProgressIndicator());
                PluginInstaller.installAfterRestart(downloader.getFile(), true, null, codenarcDescriptor);
                willInstallCodenarclugin = restart = true;
            }

            if (PluginManager.isDisabled(codenarcId)) {
                PluginManager.enablePlugin(codenarcId);
                restart = true;
            }

            if (!willInstallCodenarclugin) {
                Optional<PluginDownloader> updateDownloader = UpdateChecker.getPluginUpdates().stream().filter(d -> d.getPluginId().equals(codenarcId)).findFirst();
                if (updateDownloader.isPresent()) {
                    PluginDownloader downloader = updateDownloader.get();
                    downloader.prepareToInstall(new EmptyProgressIndicator());
                    PluginInstaller.installAfterRestart(downloader.getFile(), true, PluginManager.getPlugin(codenarcPlugin).getPath(), downloader.getDescriptor());
                }
            }

            if (restart || (PluginManager.isPluginInstalled(codenarcPlugin) && !PluginManager.isDisabled(codenarcId))) {
                IdeaPluginDescriptor codenarcUpdatedPlugin = PluginManager.getPlugin(PluginId.getId("dk.demus.idea.CodeNarc"));
                PluginInstaller.prepareToUninstall(Objects.requireNonNull(codenarcUpdatedPlugin));
                restart = true;
            }

            if (restart) {
                ApplicationManagerEx.getApplicationEx().restart(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
