package transformationProject;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.gmt.modisco.omg.kdm.action.impl.ActionPackageImpl;
import org.eclipse.gmt.modisco.omg.kdm.build.impl.BuildPackageImpl;
import org.eclipse.gmt.modisco.omg.kdm.code.impl.CodePackageImpl;
import org.eclipse.gmt.modisco.omg.kdm.conceptual.impl.ConceptualPackageImpl;
import org.eclipse.gmt.modisco.omg.kdm.core.impl.CorePackageImpl;
import org.eclipse.gmt.modisco.omg.kdm.data.impl.DataPackageImpl;
import org.eclipse.gmt.modisco.omg.kdm.event.impl.EventPackageImpl;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmPackage;
import org.eclipse.gmt.modisco.omg.kdm.kdm.impl.KdmPackageImpl;
import org.eclipse.gmt.modisco.omg.kdm.platform.impl.PlatformPackageImpl;
import org.eclipse.gmt.modisco.omg.kdm.source.impl.SourcePackageImpl;
import org.eclipse.gmt.modisco.omg.kdm.structure.impl.StructurePackageImpl;
import org.eclipse.gmt.modisco.omg.kdm.ui.impl.UiPackageImpl;
import org.eclipse.m2m.atl.emftvm.EmftvmFactory;
import org.eclipse.m2m.atl.emftvm.ExecEnv;
import org.eclipse.m2m.atl.emftvm.Metamodel;
import org.eclipse.m2m.atl.emftvm.Model;
import org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.util.ClassModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.DefaultModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.EMFTVMUtil;
import org.eclipse.m2m.atl.emftvm.util.ModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.TimingData;


public class ATLLauncher {
	
	// Some constants for quick initialization and testing.
	public final static String IN_METAMODEL = "./metamodels/kdm.ecore";
	public final static String IN_METAMODEL_NAME = "KDMMetamodel";
	public final static String OUT_METAMODEL = "./metamodels/Graph.ecore";
	public final static String OUT_METAMODEL_NAME = "GraphMetamodel";
	
	public final static String IN_MODEL = "./models/toyProject_kdm.xmi";
	public final static String OUT_MODEL = "./models/toyProject_kdm_Out.xmi";
	
	public final static String TRANSFORMATION_DIR = "./transformations/";
	public final static String TRANSFORMATION_MODULE= "KDM2GraphTransformation";
	
	// The input and output metamodel nsURIs are resolved using lazy registration of metamodels, see below.
	private String inputMetamodelNsURI;
	private String outputMetamodelNsURI;
	
	//Main transformation launch method
	public void launch(String inMetamodelPath, String inModelPath, String outMetamodelPath,
			String outModelPath, String transformationDir, String transformationModule){
		
		
		ExecEnv env = EmftvmFactory.eINSTANCE.createExecEnv();
		ResourceSet rs = new ResourceSetImpl();

		
		
		Metamodel inMetamodel = EmftvmFactory.eINSTANCE.createMetamodel();
		inMetamodel.setResource(rs.getResource(URI.createURI(inputMetamodelNsURI), true));
		env.registerMetaModel(IN_METAMODEL_NAME, inMetamodel);
		
		Model inOutModel = EmftvmFactory.eINSTANCE.createModel();
		inOutModel.setResource(rs.getResource(URI.createURI(inputMetamodelNsURI, true), true));
		env.registerInOutModel(IN_METAMODEL_NAME, inOutModel);
		
		Metamodel outMetamodel = EmftvmFactory.eINSTANCE.createMetamodel();
		outMetamodel.setResource(rs.getResource(URI.createURI(outputMetamodelNsURI), true));
		env.registerMetaModel(OUT_METAMODEL_NAME, outMetamodel);
		
		/*
		 * Create and register resource factories to read/parse .xmi and .emftvm files,
		 * we need an .xmi parser because our in/output models are .xmi and our transformations are
		 * compiled using the ATL-EMFTV compiler that generates .emftvm files
		 */
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xml", new XMLResourceFactoryImpl());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("emftvm", new EMFTVMResourceFactoryImpl());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new EcoreResourceFactoryImpl());
		rs.getPackageRegistry().put("http://www.eclipse.org/MoDisco/kdm/kdm", KdmPackageImpl.eINSTANCE);
		rs.getPackageRegistry().put("http://www.eclipse.org/MoDisco/kdm/action", ActionPackageImpl.eINSTANCE);
		rs.getPackageRegistry().put("http://www.eclipse.org/MoDisco/kdm/build", BuildPackageImpl.eINSTANCE);
		rs.getPackageRegistry().put("http://www.eclipse.org/MoDisco/kdm/code", CodePackageImpl.eINSTANCE);
		rs.getPackageRegistry().put("http://www.eclipse.org/MoDisco/kdm/conceptual", ConceptualPackageImpl.eINSTANCE);
		rs.getPackageRegistry().put("http://www.eclipse.org/MoDisco/kdm/core", CorePackageImpl.eINSTANCE);
		rs.getPackageRegistry().put("http://www.eclipse.org/MoDisco/kdm/platform", PlatformPackageImpl.eINSTANCE);
		rs.getPackageRegistry().put("http://www.eclipse.org/MoDisco/kdm/data", DataPackageImpl.eINSTANCE);
		rs.getPackageRegistry().put("http://www.eclipse.org/MoDisco/kdm/event", EventPackageImpl.eINSTANCE);
		rs.getPackageRegistry().put("http://www.eclipse.org/MoDisco/kdm/source", SourcePackageImpl.eINSTANCE);
		rs.getPackageRegistry().put("http://www.eclipse.org/MoDisco/kdm/structure", StructurePackageImpl.eINSTANCE);
		rs.getPackageRegistry().put("http://www.eclipse.org/MoDisco/kdm/ui", UiPackageImpl.eINSTANCE);
		
		KdmPackage.eINSTANCE.eResource();
		
		EMFTVMUtil.registerEPackages(rs);
		
		// Load models
		Model inModel = EmftvmFactory.eINSTANCE.createModel();
		inModel.setResource(rs.getResource(URI.createURI(inModelPath, true), true));
		env.registerInputModel("IN", inModel);
		
		Model outModel = EmftvmFactory.eINSTANCE.createModel();
		outModel.setResource(rs.createResource(URI.createURI(outModelPath)));
		env.registerOutputModel("OUT", outModel);
		
		
		
		ModuleResolver mr = new DefaultModuleResolver(transformationDir, rs);
		TimingData td = new TimingData();
		env.loadModule(mr, TRANSFORMATION_MODULE);
		td.finishLoading();
		env.run(td);
		td.finish();
			
		// Save models
		try {
			inOutModel.getResource().save(Collections.emptyMap());
			outModel.getResource().save(Collections.emptyMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private String lazyMetamodelRegistration(String metamodelPath){
		
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
   	
	    ResourceSet rs = new ResourceSetImpl();
	    final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(EPackage.Registry.INSTANCE);
	    rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
	
	    Resource r = rs.getResource(URI.createFileURI(metamodelPath), true);
	    EObject eObject = r.getContents().get(0);
	    // A meta-model might have multiple packages we assume the main package is the first one listed
	    if (eObject instanceof EPackage) {
	        EPackage p = (EPackage)eObject;
	        System.out.println(p.getNsURI());
	        EPackage.Registry.INSTANCE.put(p.getNsURI(), p);
	        return p.getNsURI();
	    }
	    return null;
	}
	
	
	public void registerInputMetamodel(String inputMetamodelPath){	
		inputMetamodelNsURI = lazyMetamodelRegistration(inputMetamodelPath);
		System.out.println("Meu Input " + inputMetamodelNsURI);
	}

	public void registerOutputMetamodel(String outputMetamodelPath){
		outputMetamodelNsURI = lazyMetamodelRegistration(outputMetamodelPath);
		System.out.println("Meu output " + outputMetamodelNsURI);
	}
	
	
	public static void main(String ... args){
		ATLLauncher l = new ATLLauncher();
		l.registerInputMetamodel(IN_METAMODEL);
		l.registerOutputMetamodel(OUT_METAMODEL);
		l.launch(IN_METAMODEL, IN_MODEL, OUT_METAMODEL, OUT_MODEL, TRANSFORMATION_DIR, TRANSFORMATION_MODULE);
	}
}
