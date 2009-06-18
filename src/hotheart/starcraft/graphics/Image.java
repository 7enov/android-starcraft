/**
 * @author Korshakov Stepan
 * Module for loading Images as a container of some GRPContainers.
 * With layering of them 
 */
package hotheart.starcraft.graphics;

import java.io.FileInputStream;
import java.io.IOException;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.files.DatFile;
import hotheart.starcraft.graphics.render.Render;
import hotheart.starcraft.graphics.render.simple.GrpRenderFactory;
import hotheart.starcraft.graphics.script.ImageScriptEngine;
import hotheart.starcraft.graphics.script.ImageState;
import hotheart.starcraft.system.ObjectPool;
import android.graphics.Canvas;

public class Image {

	public static final int MAX_IMAGE_LAYER = 10000;
	public static final int MIN_IMAGE_LAYER = 0;

	// Data from images.dat file
	private static final int COUNT = 999;

	private static int[] grpFileId;
	private static byte[] gfxTurns;
	// private static byte[] clickable;
	// private static byte[] useFullISCript;
	// private static byte[] drawIfCloacked;
	private static byte[] drawFunc;
	private static byte[] remappingData;
	private static int[] iScriptId;

	// private static int[] shieldOverlay;
	// private static int[] attackOverlay;
	// private static int[] damageOverlay;
	// private static int[] SpecialOverlay;
	// private static int[] landingDustOverlay;
	// private static int[] liftOffOverlay;

	public static void initImages(FileInputStream _is) throws IOException {
		StarcraftPalette.initPalette();
		initBuffers(_is);
	}

	private static void initBuffers(FileInputStream _is) throws IOException {
		DatFile file = new DatFile(_is);
		grpFileId = file.read4ByteData(COUNT);
		gfxTurns = file.read1ByteData(COUNT);

		// Seek unused sections
		file.skip(COUNT * 3);
		// clickable = file.read1ByteData(COUNT, buff);
		// useFullISCript = file.read1ByteData(COUNT, buff);
		// drawIfCloacked = file.read1ByteData(COUNT, buff);

		drawFunc = file.read1ByteData(COUNT);
		remappingData = file.read1ByteData(COUNT);
		iScriptId = file.read4ByteData(COUNT);

		// Unused sections
		// shieldOverlay = file.read4ByteData(COUNT, buff);
		// attackOverlay = file.read4ByteData(COUNT, buff);
		// damageOverlay = file.read4ByteData(COUNT, buff);
		// SpecialOverlay = file.read4ByteData(COUNT, buff);
		// landingDustOverlay = file.read4ByteData(COUNT, buff);
		// liftOffOverlay = file.read4ByteData(COUNT, buff);
	}

	public final static Image getImage(int id, int color, int layer) {
		int grpId = grpFileId[id];
		int scriptId = iScriptId[id];
		int align = gfxTurns[id] & 0xFF;
		int functionId = drawFunc[id] & 0xFF;
		int remapping = remappingData[id] & 0xFF;

		ImageStaticData data = new ImageStaticData(id, Render.defaultRender
				.createObject(grpId), ImageScriptEngine.createHeader(scriptId),
				functionId, remapping, align == 1);

		Image res = new Image(layer, data);

		// res.align = align == 1;
		// res.graphicsFuntion = functionId;
		// res.remapping = remapping;

		if (BuildParameters.CACHE_GRP) {

			int[] pal = StarcraftPalette.normalPalette;

			if (functionId == 10)
				pal = StarcraftPalette.shadowPalette;
			else if (functionId == 9) {
				switch (remapping) {
				case 1:
					pal = StarcraftPalette.ofirePalette;
					break;
				case 2:
					pal = StarcraftPalette.gfirePalette;
					break;
				case 3:
					pal = StarcraftPalette.bfirePalette;
					break;
				case 4:
					pal = StarcraftPalette.bexplPalette;
					break;
				}
			}

			// res.imageData.grp.image.makeCache(pal);
			// res.imageData.grp.image.makeCache(StarcraftPalette.blendedPalette);
		}

		res.foregroundColor = color;
		return res;
	}

	public Image(int imageLayer, ImageStaticData data) {

		this.imageData = data;
		this.imageState = new ImageState(this, data.scriptHeader);
		this.imageId = imageData.imageId;

		ImageScriptEngine.init(this.imageState);
		currentImageLayer = imageLayer;
	}

	public Image(Image src) {
		this.childCount = src.childCount;
		this.childs = src.childs;
		this.currentImageLayer = src.currentImageLayer;
		this.deleted = src.deleted;
		this.foregroundColor = src.foregroundColor;
		this.imageState = new ImageState(src.imageState);
		this.imageState.image = this;
		this.offsetX = src.offsetX;
		this.offsetY = src.offsetY;
		this.parentOverlay = src.parentOverlay;
		this.posX = src.posX;
		this.posY = src.posY;
		this.sortIndex = src.sortIndex;
		this.imageData = src.imageData;// Don't copy!
		this.imageId = src.imageId;
	}

	public int imageId = 0;

	public int foregroundColor;

	public int currentImageLayer;

	private ImageStaticData imageData;

	// Offset from main offset

	protected int offsetX = 0;
	protected int offsetY = 0;

	public void setOffset(int dx, int dy) {
		offsetX = dx;
		offsetY = dy;
	}

	public void setOffsetX(int dx) {
		offsetX = dx;
	}

	public void setOffsetY(int dy) {
		offsetY = dy;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	// Global positions
	protected int posX = 0;
	protected int posY = 0;

	public void setPos(int dx, int dy) {
		posX = dx;
		posY = dy;
	}

	public void setPosX(int dx) {
		posX = dx;
	}

	public void setPosY(int dy) {
		posY = dy;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	// Children data
	public Image[] childs = new Image[20];

	// Graphics script data
	public ImageState imageState;
	// Game Data
	public Image parentOverlay = null;

	public boolean deleted = false;

	public int childCount = 0;

	public void delete() {

		deleted = true;
		ObjectPool.removeImage(this);
		if (childCount == 0) {
			if (parentOverlay != null) {
				parentOverlay.removeChild(this);
			}
		}
	}

	public final void addOverlay(Image img) {
		for (int i = 0; i < childs.length; i++)
			if (childs[i] == null) {
				childs[i] = img;
				childCount++;
				return;
			}

	}

	public final void addUnderlay(Image img) {
		for (int i = 0; i < childs.length; i++)
			if (childs[i] == null) {
				childCount++;
				childs[i] = img;
				return;
			}
	}

	public final void removeChild(Image img) {
		for (int i = 0; i < childs.length; i++)
			if (childs[i] == img) {
				childs[i] = null;
				childCount--;
				break;
			}
		if ((deleted) & (childCount == 0))
			delete();
	}

	public void update() {
		if (!deleted)
			ImageScriptEngine.exec(this.imageState);

		for (int i = 0; i < childs.length; i++)
			if (childs[i] != null)
				childs[i].update();

	}

	public final void play(int anim) {
		if (!deleted)
			ImageScriptEngine.play(this.imageState, anim);

		for (int i = 0; i < childs.length; i++)
			if (childs[i] != null)
				if (childs[i].imageState.followParentAnim)
					childs[i].play(anim);
	}

	public int sortIndex = 0;

	public void preDraw() {
		if (!this.imageState.visible)
			return;

		// setPos(dX, dY);

		ObjectPool.drawObjects.add(this);

		for (int i = 0; i < childs.length; i++)
			if (childs[i] != null) {
				childs[i].setPos(this.posX, this.posY);
				childs[i].preDraw();
			}
	}

	public void drawWithoutChilds(Canvas c) {
		if (!imageState.isBlocked)
			if (parentOverlay != null) {
				if (imageState.followParent)
					imageState.baseFrame = parentOverlay.imageState.baseFrame;
				if (imageState.followParentAngle)
					imageState.angle = parentOverlay.imageState.angle;
			}

		if (!deleted) {
			imageData.renderImage.draw(posX + offsetX, posY + offsetY,
					this.imageData.align, imageState.baseFrame,
					imageState.angle, this.imageData.graphicsFuntion,
					this.imageData.remapping, this.foregroundColor);
		}
	}

	public void draw(Canvas c) {
		if (!imageState.visible)
			return;

		if (!imageState.isBlocked)
			if (parentOverlay != null) {
				if (imageState.followParent)
					imageState.baseFrame = parentOverlay.imageState.baseFrame;
				if (imageState.followParentAngle)
					imageState.angle = parentOverlay.imageState.angle;
			}

		for (int i = 0; i < childs.length; i++)
			if (childs[i] != null) {
				childs[i].setPos(this.posX, this.posY);
				childs[i].draw(c);
			}

		drawWithoutChilds(c);
	}
}
