package br.edu.univercidade.cc.xithcluster.samples;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loaders.models.ModelLoader;
import org.xith3d.loop.opscheduler.Animator;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.schedops.movement.AnimatableGroup;
import org.xith3d.schedops.movement.GroupRotator;
import org.xith3d.schedops.movement.TransformationDirectives;

import br.edu.univercidade.cc.xithcluster.SampleApplication;
import br.edu.univercidade.cc.xithcluster.utils.SceneUtils;

public class JusticeLeagueSample extends SampleApplication {

	@Override
	protected String getJARName() {
		return "";
	}

	@Override
	protected BranchGroup createSceneRoot(Animator animator) {
		BranchGroup root;
		Group group;

		root = new BranchGroup();

		SceneUtils.addDirectionalLight(root, "globalLight", new Colorf(1.0f,
				1.0f, 1.0f), new Vector3f(0.707f, -0.707f, 0.0f));

		group = new TransformGroup(new Tuple3f(0.0f, -1.0f, 0.0f));
		group.setName("allModels");
		root.addChild(group);

		loadOBJIntoScene("Batman", new Vector3f(-2.0f, 0.0f, 0.0f), group, animator);
		loadOBJIntoScene("Superman", new Vector3f(-1.0f, 0.0f, 0.0f), group, animator);
		loadOBJIntoScene("GreenLantern", new Vector3f(0.0f, 0.0f, 0.0f), group, animator);
		loadOBJIntoScene("WonderWoman", new Vector3f(1.0f, 0.0f, 0.0f), group, animator);
		loadOBJIntoScene("Flash", new Vector3f(2.0f, 0.0f, 0.0f), group, animator);
		
		return root;
	}

	private void loadOBJIntoScene(String name, Vector3f position, Group parent, Animator animator) {
		AnimatableGroup modelGroup;
		GroupRotator groupRotation;
		groupRotation = new GroupRotator(new TransformationDirectives(
				new Vector3f(0.0f, 1.0f, 0.0f), 0.0f, 0.1f));
		modelGroup = new AnimatableGroup(groupRotation);
		modelGroup.setTranslation(position);
		parent.addChild(modelGroup);
		animator.addAnimatableObject(modelGroup);
		try {
			modelGroup.addChild(ModelLoader.getInstance().loadModel(
					"resources/models/" + name + "/" + name + ".obj", 0.5f));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
