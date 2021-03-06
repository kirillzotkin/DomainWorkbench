package org.domainworkbench.handlers;

import org.classupplier.ClassSupplier;
import org.domainworkbench.wizards.NewEPackageWizard;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.wizard.WizardDialog;

public class NewEPackageHandler {
	@Execute
	public void execute(IShellProvider shellProvider, ClassSupplier classupplier, EPartService partService) {
		WizardDialog wizard = new WizardDialog(shellProvider.getShell(),
				new NewEPackageWizard(classupplier, partService));
		wizard.open();
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}

}