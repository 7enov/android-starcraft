package hotheart.starcraft.graphics.render.simple;

import android.content.Context;
import android.graphics.Canvas;
import hotheart.starcraft.graphics.render.RenderImage;
import hotheart.starcraft.graphics.render.Render;
import hotheart.starcraft.graphics.render.ViewController;

public class SimpleRender extends Render {

	public Canvas canvas = null;
	private SimpleViewController contr = null;

	@Override
	public void begin() {
	}

	@Override
	public void end() {
	}

	@Override
	public RenderImage createObject(int grpId) {
		return new SimpleRenderImage(this, GrpRenderFactory.getGraphics(grpId));
	}

	@Override
	protected ViewController createViewController(Context c) {
		contr = new SimpleViewController(c, this);
		return contr;
	}
}