/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.studio.engine.export.switcher;

import java.util.Set;

import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder;
import org.bonitasoft.studio.model.process.AbstractProcess;
import org.bonitasoft.studio.model.process.Element;
import org.bonitasoft.studio.model.process.Pool;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Romain Bioteau
 */
public class AbstractProcessSwitch extends AbstractSwitch {

    protected final ProcessDefinitionBuilder builder;

    public AbstractProcessSwitch(final ProcessDefinitionBuilder processBuilder, final Set<EObject> eObjectNotExported) {
        super(eObjectNotExported);
        builder = processBuilder;
    }

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.studio.model.process.util.ProcessSwitch#casePool(org.bonitasoft.studio.model.process.Pool)
     */
    @Override
    public Element casePool(final Pool pool) {
        addDocuments(builder, pool);
        return pool;
    }

    @Override
    public Element caseAbstractProcess(final AbstractProcess process) {
        addActors(builder, process);
        addData(builder, process);
        addParameters(builder, process);
        addConnector(builder, process);
        addKPIBinding(builder, process);
        if (process instanceof Pool) {
            addContract(builder, (Pool) process);
            addContext(builder, (Pool) process);
        }
        return process;
    }

    public ProcessDefinitionBuilder getProcessBuilder() {
        return builder;
    }
}
