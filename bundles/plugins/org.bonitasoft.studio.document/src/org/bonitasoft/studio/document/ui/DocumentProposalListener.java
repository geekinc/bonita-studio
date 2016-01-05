/**
 * Copyright (C) 2014 Bonitasoft S.A.
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
package org.bonitasoft.studio.document.ui;

import org.bonitasoft.studio.expression.editor.provider.IProposalListener;
import org.bonitasoft.studio.model.process.Document;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;

public class DocumentProposalListener implements IProposalListener {

    @Override
    public String handleEvent(final EObject context, final String fixedReturnType) {
        Assert.isNotNull(context);
        final DocumentWizard documentWizard = createDocumentWizard(context);
        final Dialog documentWizardDialog = createDocumentWizardDialog(documentWizard);
        if (documentWizardDialog.open() == Dialog.OK) {
            final Document document = documentWizard.getDocument();
            if (document != null) {
                return document.getName();
            }
        }
        return null;
    }

    protected DocumentWizard createDocumentWizard(final EObject context) {
        return new DocumentWizard(context);
    }

    protected DocumentWizardDialog createDocumentWizardDialog(final DocumentWizard documentWizard) {
        return new DocumentWizardDialog(Display.getCurrent().getActiveShell().getParent().getShell(),
                documentWizard, false);
    }

    @Override
    public boolean isRelevant(final EObject context) {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.studio.expression.editor.provider.IProposalListener#setEStructuralFeature(org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public void setEStructuralFeature(EStructuralFeature feature) {

    }

}
