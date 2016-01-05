/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.studio.expression.editor.formfield;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bonitasoft.studio.common.ExpressionConstants;
import org.bonitasoft.studio.common.emf.tools.WidgetHelper;
import org.bonitasoft.studio.common.jface.TableColumnSorter;
import org.bonitasoft.studio.expression.core.provider.IExpressionEditor;
import org.bonitasoft.studio.expression.core.scope.ExpressionScope;
import org.bonitasoft.studio.expression.editor.i18n.Messages;
import org.bonitasoft.studio.expression.editor.provider.SelectionAwareExpressionEditor;
import org.bonitasoft.studio.model.expression.Expression;
import org.bonitasoft.studio.model.expression.ExpressionPackage;
import org.bonitasoft.studio.model.form.Widget;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * @author Romain Bioteau
 * 
 */
public class FormFieldExpressionEditor extends SelectionAwareExpressionEditor implements IExpressionEditor {

    private Composite mainComposite;

    private TableViewer viewer;

    private final ComposedAdapterFactory adapterFactory;

    private final AdapterFactoryLabelProvider adapterLabelProvider;

    private Expression inputExpression;

    private Text typeText;

    public FormFieldExpressionEditor() {
        adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
        adapterLabelProvider = new AdapterFactoryLabelProvider(adapterFactory);
    }

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.studio.expression.editor.provider.IExpressionEditor#createExpressionEditor(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public Control createExpressionEditor(Composite parent, EMFDataBindingContext ctx) {
        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        mainComposite.setLayout(new GridLayout(1, false));

        viewer = new TableViewer(mainComposite, SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
        viewer.getTable().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

        final TableLayout layout = new TableLayout();
        layout.addColumnData(new ColumnWeightData(100, false));
        viewer.getTable().setLayout(layout);
        viewer.getTable().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

        final TableViewerColumn columnViewer = new TableViewerColumn(viewer, SWT.NONE);
        final TableColumn column = columnViewer.getColumn();
        column.setText(Messages.name);

        final TableColumnSorter sorter = new TableColumnSorter(viewer);
        sorter.setColumn(column);

        viewer.getTable().setHeaderVisible(true);

        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(adapterLabelProvider);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent arg0) {
                FormFieldExpressionEditor.this.fireSelectionChanged();
            }
        });

        createReturnTypeComposite(parent);

        return mainComposite;
    }

    protected void createReturnTypeComposite(Composite parent) {
        final Composite typeComposite = new Composite(parent, SWT.NONE);
        typeComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        final GridLayout gl = new GridLayout(2, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        typeComposite.setLayout(gl);

        final Label typeLabel = new Label(typeComposite, SWT.NONE);
        typeLabel.setText(Messages.returnType);
        typeLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).create());

        typeText = new Text(typeComposite, SWT.BORDER | SWT.READ_ONLY);
        typeText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

    }

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.studio.expression.editor.provider.IExpressionEditor#bindExpression(org.eclipse.emf.databinding.EMFDataBindingContext,
     * org.eclipse.emf.ecore.EObject, org.bonitasoft.studio.model.expression.Expression, org.eclipse.emf.edit.domain.EditingDomain)
     */
    @Override
    public void bindExpression(EMFDataBindingContext dataBindingContext, Expression inputExpression, ExpressionScope scope) {
        this.inputExpression = inputExpression;
        final Set<Widget> input = new HashSet<Widget>();
        final Set<String> widgetName = new HashSet<String>();
        for (final Expression e : scope.getExpressionsWithType(ExpressionConstants.FORM_FIELD_TYPE)) {
            final Widget widget = (Widget) e.getReferencedElements().get(0);
            if (inputExpression.isReturnTypeFixed()) {
                if (!widgetName.contains(widget.getName()) && compatibleReturnType(inputExpression, e)) {
                    input.add(widget);
                    widgetName.add(widget.getName());
                }
            } else {
                if (!widgetName.contains(widget.getName())) {
                    input.add(widget);
                    widgetName.add(widget.getName());
                }
            }
        }
        viewer.setInput(input);

        final IObservableValue contentObservable = EMFObservables.observeValue(inputExpression, ExpressionPackage.Literals.EXPRESSION__CONTENT);
        final IObservableValue nameObservable = EMFObservables.observeValue(inputExpression, ExpressionPackage.Literals.EXPRESSION__NAME);
        final IObservableValue returnTypeObservable = EMFObservables.observeValue(inputExpression, ExpressionPackage.Literals.EXPRESSION__RETURN_TYPE);
        final IObservableValue referenceObservable = EMFObservables.observeValue(inputExpression, ExpressionPackage.Literals.EXPRESSION__REFERENCED_ELEMENTS);

        final UpdateValueStrategy selectionToName = new UpdateValueStrategy();

        final IConverter nameConverter = new Converter(Widget.class, String.class) {

            @Override
            public Object convert(Object widget) {
                return ((Widget) widget).getName();
            }

        };
        selectionToName.setConverter(nameConverter);

        final UpdateValueStrategy selectionToContent = new UpdateValueStrategy();
        final IConverter contentConverter = new Converter(Widget.class, String.class) {

            @Override
            public Object convert(Object widget) {
                return WidgetHelper.FIELD_PREFIX + ((Widget) widget).getName();
            }

        };
        selectionToContent.setConverter(contentConverter);

        final UpdateValueStrategy selectionToReturnType = new UpdateValueStrategy();
        final IConverter returnTypeConverter = new Converter(Widget.class, String.class) {

            @Override
            public Object convert(Object widget) {
                return WidgetHelper.getAssociatedReturnType((Widget) widget);
            }

        };
        selectionToReturnType.setConverter(returnTypeConverter);

        final UpdateValueStrategy selectionToReferencedData = new UpdateValueStrategy();
        final IConverter referenceConverter = new Converter(Widget.class, List.class) {

            @Override
            public Object convert(Object widget) {
                return Collections.singletonList(widget);
            }

        };
        selectionToReferencedData.setConverter(referenceConverter);

        final UpdateValueStrategy referencedDataToSelection = new UpdateValueStrategy();
        final IConverter referencetoDataConverter = new Converter(List.class, Widget.class) {

            @Override
            public Object convert(Object widgetList) {
                final Widget w = ((List<Widget>) widgetList).get(0);
                final Collection<Widget> inputData = (Collection<Widget>) viewer.getInput();
                for (final Widget widget : inputData) {
                    if (widget.getName().equals(w.getName())
                            && WidgetHelper.getAssociatedReturnType(widget).equals(WidgetHelper.getAssociatedReturnType(w))) {
                        return widget;
                    }
                }
                return null;
            }

        };
        referencedDataToSelection.setConverter(referencetoDataConverter);
        dataBindingContext.bindValue(SWTObservables.observeText(typeText, SWT.Modify), returnTypeObservable);
        dataBindingContext.bindValue(ViewersObservables.observeSingleSelection(viewer), nameObservable, selectionToName, new UpdateValueStrategy(
                UpdateValueStrategy.POLICY_NEVER));
        dataBindingContext.bindValue(ViewersObservables.observeSingleSelection(viewer), contentObservable, selectionToContent, new UpdateValueStrategy(
                UpdateValueStrategy.POLICY_NEVER));
        dataBindingContext.bindValue(ViewersObservables.observeSingleSelection(viewer), returnTypeObservable, selectionToReturnType, new UpdateValueStrategy(
                UpdateValueStrategy.POLICY_NEVER));
        dataBindingContext.bindValue(ViewersObservables.observeSingleSelection(viewer), referenceObservable, selectionToReferencedData,
                referencedDataToSelection);
    }

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.studio.expression.editor.provider.IExpressionEditor#canFinish()
     */
    @Override
    public boolean canFinish() {
        return !viewer.getSelection().isEmpty();
    }

    @Override
    public void okPressed() {
        if (!inputExpression.getContent().equals(inputExpression.getName())) {
            inputExpression.setName(inputExpression.getContent());
        }
    }

    @Override
    public Control getTextControl() {
        return null;
    }

}
