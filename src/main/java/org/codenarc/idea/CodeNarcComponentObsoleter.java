package org.codenarc.idea;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginInstaller;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.ide.plugins.PluginNode;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.updateSettings.impl.PluginDownloader;

import java.io.IOException;
import java.util.Objects;

//import com.intellij.ide.plugins.PluginManagerConfigurable;

public class CodeNarcComponentObsoleter implements BaseComponent {

    public void initComponent() {
        try {
            PluginId codenarcId = PluginId.getId("CodeNarc");
            IdeaPluginDescriptor codenarcPlugin;
            if (!PluginManagerCore.isPluginInstalled(codenarcId)) {
                codenarcPlugin = new PluginNode(codenarcId);
                PluginDownloader downloader = PluginDownloader.createDownloader(codenarcPlugin);
                downloader.prepareToInstall(new EmptyProgressIndicator());
                PluginInstaller.installAfterRestart(downloader.getFile(), true, null, codenarcPlugin);
            }
            else {
                codenarcPlugin = PluginManager.getPlugin(codenarcId);
                if (codenarcPlugin != null && !codenarcPlugin.isEnabled()) {
                    codenarcPlugin.setEnabled(true);
                }
            }
            IdeaPluginDescriptor codenarcUpdatedPlugin = PluginManager.getPlugin(PluginId.getId("dk.demus.idea.CodeNarc"));
            PluginInstaller.prepareToUninstall(Objects.requireNonNull(codenarcUpdatedPlugin));
            ApplicationManagerEx.getApplicationEx().restart(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
