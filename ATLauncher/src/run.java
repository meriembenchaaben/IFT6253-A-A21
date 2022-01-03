
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.modisco.infra.discovery.core.*;
import org.eclipse.modisco.infra.discovery.core.annotations.ParameterInitialValue;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.modisco.kdm.source.*;
import org.eclipse.modisco.java.*;
/*import org.eclipse.modisco.java.composition.discoverer;*/
import org.eclipse.modisco.java.composition.discoverer.DiscoverKDMSourceAndJavaModelFromJavaProject;
import org.eclipse.modisco.java.discoverer.DiscoverJavaModelFromJavaProject;
import org.eclipse.modisco.java.generation.files.GenerateJavaExtended;


public class run {

	public static void modelDiscovery(String projectName, String savePath){
		try {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
	
		    System.out.println(projects[0].getName());
			IProject project = 
				ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			IJavaProject javaProject = JavaCore.create(project);
			/*DiscoverJavaModelFromJavaProject javaDiscoverer = new DiscoverJavaModelFromJavaProject();*/
			DiscoverKDMSourceAndJavaModelFromJavaProject javaDiscoverer = new DiscoverKDMSourceAndJavaModelFromJavaProject();

			javaDiscoverer.discoverElement(javaProject, new NullProgressMonitor());
			Resource javaResource = javaDiscoverer.getTargetModel();
			FileOutputStream fout = new FileOutputStream(new File(savePath));
			javaResource.save(fout, null);
			fout.close();

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void generateJava(String javaModelFilepath, String generatedCodeFolderPath){
		try {
			GenerateJavaExtended javaGenerator = new GenerateJavaExtended(URI.createFileURI(javaModelFilepath),
					new File(generatedCodeFolderPath),
					new ArrayList<Object>());
			javaGenerator.doGenerate(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	public static void main (String[] args) {

		modelDiscovery("toyProject","please_Results/");
}
	
}


