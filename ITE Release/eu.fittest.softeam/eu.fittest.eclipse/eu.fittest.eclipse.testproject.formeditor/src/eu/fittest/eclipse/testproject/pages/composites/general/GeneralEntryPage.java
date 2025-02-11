package eu.fittest.eclipse.testproject.pages.composites.general;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.composites.EMFPropertyStringComposite;
import eu.fittest.eclipse.testproject.pages.GeneralPage;
import eu.fittest.eclipse.testproject.pages.composites.GeneralComposite;
import eu.fittest.test.project.ProjectPackage;

public class GeneralEntryPage extends EMFPropertyStringComposite {

	public GeneralEntryPage(GeneralComposite parent, int style, FormToolkit toolkit,
			EObject model, GeneralPage page) {
		super(parent, style, toolkit, model, page);
	}

	@Override
	protected EStructuralFeature getFeature() {
		return ProjectPackage.eINSTANCE.getGeneralType_EntryPage();
	}

	@Override
	protected String getLabelText() {
		return "Entry page: ";
	}

}
