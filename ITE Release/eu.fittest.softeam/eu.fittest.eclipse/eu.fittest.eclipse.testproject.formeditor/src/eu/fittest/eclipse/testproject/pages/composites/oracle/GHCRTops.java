package eu.fittest.eclipse.testproject.pages.composites.oracle;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.composites.EMFPropertyStringComposite;
import eu.fittest.eclipse.testproject.pages.OraclePage;
import eu.fittest.eclipse.testproject.pages.composites.OracleComposite;
import eu.fittest.test.project.ProjectPackage;

public class GHCRTops extends EMFPropertyStringComposite {

	public GHCRTops(OracleComposite parent, int style, FormToolkit toolkit,
			EObject model, OraclePage page) {
		super(parent, style, toolkit, model, page);
	}

	@Override
	protected EStructuralFeature getFeature() {
		return ProjectPackage.eINSTANCE.getOracleType_GHCRTopts();
	}

	@Override
	protected String getLabelText() {
		return "GHCRT options: ";
	}

}