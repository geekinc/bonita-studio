/**
 * Copyright (C) 2015 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.studio.designer.ui.handler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import org.bonitasoft.studio.browser.operation.OpenBrowserOperation;
import org.bonitasoft.studio.common.jface.BonitaErrorDialog;
import org.bonitasoft.studio.common.jface.FileActionDialog;
import org.bonitasoft.studio.common.log.BonitaStudioLog;
import org.bonitasoft.studio.designer.UIDesignerPlugin;
import org.bonitasoft.studio.designer.core.PageDesignerURLFactory;
import org.bonitasoft.studio.designer.i18n.Messages;
import org.bonitasoft.studio.preferences.BonitaStudioPreferencesPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Romain Bioteau
 */
public class OpenUIDesignerHandler extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        execute();
        return null;
    }

    public void execute() throws ExecutionException {
        openUiDesignerInBrowser();
    }

    protected boolean waitUntilTomcatIsReady(final URL url) {
        final IProgressService service = PlatformUI.getWorkbench().getProgressService();
        try {
            service.busyCursorWhile(new IRunnableWithProgress() {

                @Override
                public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(Messages.bind(Messages.waitingForTomcatServer, org.bonitasoft.studio.common.Messages.uiDesignerModuleName),
                            IProgressMonitor.UNKNOWN);
                    connectToURL(url, monitor);
                }

                private void connectToURL(final URL url, final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        if (monitor.isCanceled()) {
                            throw new InterruptedException();
                        }
                        final HttpURLConnection connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
                        connection.setRequestMethod("HEAD");
                        connection.setReadTimeout(200);
                        final int responseCode = connection.getResponseCode();
                        if (responseCode != HttpURLConnection.HTTP_OK) {
                            connectToURL(url, monitor);
                        }
                    } catch (final IOException e) {
                        try {
                            Thread.sleep(500);
                        } catch (final InterruptedException e1) {
                        }
                        connectToURL(url, monitor);
                    }
                }
            });
        } catch (final InvocationTargetException e) {
            BonitaStudioLog.error(e);
        } catch (final InterruptedException e) {
            return false;
        }
        return true;
    }

    protected void openUiDesignerInBrowser() throws ExecutionException {
        final PageDesignerURLFactory pageDesignerURLBuilder = new PageDesignerURLFactory(getPreferenceStore());
        try {
            final URL openPageDesignerHomeURL = pageDesignerURLBuilder.openPageDesignerHome();
            if (waitUntilTomcatIsReady(openPageDesignerHomeURL)) {
                createOpenBrowserOperation(openPageDesignerHomeURL).execute();
            }
        } catch (final IOException e) {
            if (!FileActionDialog.getDisablePopup()) {
                BonitaErrorDialog.openError(Display.getDefault().getActiveShell(),
                        Messages.invalidURLTitle,
                        Messages.invalidURLMsg,
                        new Status(IStatus.ERROR, UIDesignerPlugin.PLUGIN_ID, e.getMessage(), e));
            }
            throw new ExecutionException("Failed to open web browser", e);
        }
    }

    protected OpenBrowserOperation createOpenBrowserOperation(final URL url) throws MalformedURLException {
        return new OpenBrowserOperation(url);
    }

    protected IEclipsePreferences getPreferenceStore() {
        return InstanceScope.INSTANCE.getNode(BonitaStudioPreferencesPlugin.PLUGIN_ID);
    }

}
