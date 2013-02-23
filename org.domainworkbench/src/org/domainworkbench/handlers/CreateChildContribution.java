package org.domainworkbench.handlers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.AboutToHide;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandParameter;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;

public class CreateChildContribution {

	@Inject
	protected IEditingDomainProvider editingDomainProvider;

	@Optional
	protected IEditingDomainItemProvider itemProvider;

	@Inject
	protected ECommandService commandService;

	@Inject
	protected EModelService modelService;

	@Inject
	protected MApplication app;

	private EObject eObject;

	@Inject
	public void setSelection(
			@Optional @Named(IServiceConstants.ACTIVE_SELECTION) EObject eObject) {
		this.eObject = eObject;
	}

	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) {
		items.clear();
		if (eObject == null)
			return;

		EditingDomain domain = editingDomainProvider.getEditingDomain();
		Collection<?> newChildDescriptors = domain.getNewChildDescriptors(
				eObject, null);

		if (newChildDescriptors != null) {
			for (Object descriptor : newChildDescriptors) {
				Command emfCommand = CreateChildCommand.create(domain,
						itemProvider, descriptor,
						Collections.singletonList(eObject));
				Object emfFeature = ((CommandParameter) descriptor)
						.getFeature();

				MHandledMenuItem menuItem = MMenuFactory.INSTANCE
						.createHandledMenuItem();
				menuItem.setLabel(emfCommand.getLabel());
				MCommand modeledCommand = (MCommand) modelService.find(
						"org.domainworkbench.command.createChild", app);
				menuItem.setCommand(modeledCommand);
				menuItem.setContributorURI("platform:/plugin/org.domainworkbench");
				MParameter parameter = MCommandsFactory.INSTANCE
						.createParameter();
				parameter
						.setName("org.domainworkbench.commandparameter.emfCommand");
				parameter.setValue(String.valueOf(emfCommand.hashCode()));
				menuItem.getParameters().add(parameter);
				parameter = MCommandsFactory.INSTANCE.createParameter();
				parameter
						.setName("org.domainworkbench.commandparameter.emfFeature");
				parameter.setValue(String.valueOf(emfFeature.hashCode()));
				menuItem.getParameters().add(parameter);

				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put(
						"org.domainworkbench.commandparameter.emfCommand",
						emfCommand);
				parameters.put(
						"org.domainworkbench.commandparameter.emfFeature",
						emfFeature);
				ParameterizedCommand command = commandService.createCommand(
						"org.domainworkbench.command.createChild", parameters);
				menuItem.setWbCommand(command);
				items.add(menuItem);
			}
		}
	}

	@AboutToHide
	public void aboutToHide() {

	}

}
