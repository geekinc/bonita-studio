/**
 * Copyright (C) 2013-2014 BonitaSoft S.A.
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
package org.bonitasoft.studio.properties.test;

import static org.bonitasoft.studio.properties.i18n.Messages.editDecisionTable;
import static org.bonitasoft.studio.properties.i18n.Messages.useDecisionTable;

import java.io.IOException;

import org.bonitasoft.studio.common.jface.FileActionDialog;
import org.bonitasoft.studio.decision.ui.Messages;
import org.bonitasoft.studio.expression.editor.viewer.ExpressionViewer;
import org.bonitasoft.studio.preferences.BonitaPreferenceConstants;
import org.bonitasoft.studio.preferences.BonitaStudioPreferencesPlugin;
import org.bonitasoft.studio.test.swtbot.util.SWTBotTestUtil;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.SWTBotGefTestCase;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @author Florine Boudin
 *
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class TestDecisionTable extends SWTBotGefTestCase {

    @Override
    @Before
    public void setUp() {
        BonitaStudioPreferencesPlugin.getDefault().getPreferenceStore()
                .setValue(BonitaPreferenceConstants.CONSOLE_BROWSER_CHOICE, BonitaPreferenceConstants.INTERNAL_BROWSER);
        BonitaStudioPreferencesPlugin.getDefault().getPreferenceStore().setValue(BonitaPreferenceConstants.ASK_RENAME_ON_FIRST_SAVE, false);
        FileActionDialog.setDisablePopup(true);
    }


    @Test
    public void testConditionExpressions() throws IOException, InterruptedException{

        SWTBotTestUtil.importProcessWIthPathFromClass(bot, "TestDecisionTable-1.0.bos", SWTBotTestUtil.IMPORTER_TITLE_BONITA, "TestDecisionTable", getClass(), false);

        final SWTBotEditor botEditor = bot.activeEditor();
        final SWTBotGefEditor gmfEditor = bot.gefEditor(botEditor.getTitle());

        gmfEditor.getEditPart("sf1").click();
        bot.viewById(SWTBotTestUtil.VIEWS_PROPERTIES_PROCESS_GENERAL).show();
        SWTBotTestUtil.selectTabbedPropertyView(bot, "General");

        bot.radio(useDecisionTable).click();
        bot.waitUntil(Conditions.widgetIsEnabled(bot.button(editDecisionTable)));
        bot.button(editDecisionTable).click();

        bot.waitUntil(Conditions.shellIsActive(Messages.wizardPageTitle));
        bot.waitUntil(Conditions.widgetIsEnabled(bot.link("<A>" + Messages.addRow + "</A>")));

        bot.link("<A>" + Messages.addRow + "</A>").click(Messages.addRow);

        bot.waitUntil(Conditions.widgetIsEnabled(bot.link("<A>" + Messages.addCondition + "</A>")));
        addTrueCondition(0, "true");
        addTrueCondition(1, "1==2");
        addFalseCondition(2, "sdkgjskrg");

        changeCondition(2, "myBoolean");
        testUpdateLineButtonEnabled();
        bot.toolbarButtonWithId(ExpressionViewer.SWTBOT_ID_ERASEBUTTON,0).click();
        bot.sleep(1000);
        testUpdateLineButtonNotEnabled();

        changeCondition(0, "myText==\"\"");
        testUpdateLineButtonEnabled();
        bot.button(Messages.updateLine).click();
        bot.button(IDialogConstants.FINISH_LABEL);
        SWTBotTestUtil.waitUntilBonitaBPmShellIsActive(bot);
        bot.saveAllEditors();
    }

    private void addTrueCondition(final int idx, final String condition){
        bot.link("<A>" + Messages.addCondition + "</A>").click(Messages.addCondition);

        testUpdateLineButtonNotEnabled();
        changeCondition(idx, condition);
        testUpdateLineButtonEnabled();
    }

    private void addFalseCondition(final int idx, final String condition){
        bot.link("<A>" + Messages.addCondition + "</A>").click(Messages.addCondition);

        testUpdateLineButtonNotEnabled();
        changeCondition(idx, condition);
        testUpdateLineButtonNotEnabled();
    }


    private void changeCondition(final int idx, final String condition){
        bot.text(idx).setText(condition);
        bot.sleep(1000);
    }

    private void testUpdateLineButtonEnabled(){
        Assert.assertTrue("Update Line Button should  be enabled", bot.button(Messages.updateLine).isEnabled());

    }
    private void testUpdateLineButtonNotEnabled(){
        Assert.assertTrue("Update Line Button should not be enabled", !bot.button(Messages.updateLine).isEnabled());

    }

}
