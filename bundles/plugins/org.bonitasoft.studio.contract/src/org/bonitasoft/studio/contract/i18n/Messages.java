/**
 * Copyright (C) 2014 BonitaSoft S.A.
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
package org.bonitasoft.studio.contract.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Bioteau
 */
public class Messages extends NLS {

    static {
        NLS.initializeMessages("messages", Messages.class);//$NON-NLS-1$
    }

    public static String contractSectionDescription;
    public static String name;
    public static String type;
    public static String mandatory;
    public static String multiple;
    public static String add;
    public static String remove;
    public static String savedInto;
    public static String description;
    public static String automaticMappingTooltip;
    public static String constraints;
    public static String none;
    public static String addConstraint;
    public static String editConstraint;
    public static String inputSuffix;
    public static String duplicatedInputNames;
    public static String contractInputs;
    public static String inputTabLabel;
    public static String constraintsTabLabel;
    public static String contractInputTypeLabel;
    public static String addChild;
    public static String removeInputConfirmationMessage;
    public static String removeInputConfirmationTitle;
    public static String returnType;
    public static String up;
    public static String down;
    public static String expression;
    public static String removeConstraintConfirmationTitle;
    public static String removeConstraintConfirmationMessage;
    public static String errorMessage;
    public static String noInputReferencedInConstraintExpression;
    public static String unknownInputReferencedInConstraintExpression;
    public static String duplicatedConstraintNames;
    public static String constraintEditorDescription;
    public static String addContentToConstraint;
    public static String editContentToConstraint;
    public static String emptyExpressionContent;
    public static String noContractInputReferencedInExpression;
    public static String nullable;
    public static String refactorFailedTitle;
    public static String refactorFailedMsg;
    public static String removeInputErrorTitle;
    public static String removeInputErrorMsg;
    public static String severalCompilationErrors;
    public static String SelectBusinessDataWizardPageTitle;
    public static String selectBusinessDataWizardPageDescription;
    public static String contentAssistHint;
    public static String generate;
    public static String contractInputGenerationTitle;
    public static String rootContractInputName;
    public static String selectFieldToGenerateTitle;
    public static String selectFieldToGenerateDescription;
    public static String inputOfType;
    public static String inputType;
    public static String attributetype;
    public static String atLeastOneAttributeShouldBeSelectedError;
    public static String attributeName;

}