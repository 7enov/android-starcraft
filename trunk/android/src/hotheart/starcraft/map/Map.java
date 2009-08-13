package hotheart.starcraft.map;

import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Map {

	public int width;
	public int height;

	public int[] mapTiles;

	Bitmap tile;

	public Map(byte[] mapData) {
		int p = 0;
		while (p < mapData.length) {
			long len = (mapData[p + 4] & 0xFF) + ((mapData[p + 5] & 0xFF) << 8)
					+ ((mapData[p + 6] & 0xFF) << 16)
					+ ((mapData[p + 7] & 0xFF) << 24);

			int dOffset = p + 8;

			if ((mapData[p] == 'E') & (mapData[p + 1] == 'R')
					& (mapData[p + 2] == 'A')) {

				int era = (mapData[dOffset] & 0xFF)
						+ ((mapData[dOffset + 1] & 0xFF) << 8);

				era = era % 8;

				TileLib.init(era);
			} else if ((mapData[p] == 'D') & (mapData[p + 1] == 'I')
					& (mapData[p + 2] == 'M')) {
				width = (mapData[dOffset] & 0xFF)
						+ ((mapData[dOffset + 1] & 0xFF) << 8);
				height = (mapData[dOffset + 2] & 0xFF)
						+ ((mapData[dOffset + 3] & 0xFF) << 8);
			} else if ((mapData[p] == 'T') & (mapData[p + 1] == 'I')
					& (mapData[p + 2] == 'L') & (mapData[p + 3] == 'E')) {
				Log.v("Size:", width + "x" + height);
				mapTiles = new int[width * height];

				for (int i = 0; i < mapTiles.length; i++) {
					mapTiles[i] = (mapData[dOffset + i * 2] & 0xFF)
							+ ((mapData[dOffset + i * 2 + 1] & 0xFF) << 8);
				}
			}

			p += 8 + len;
		}
	}

	public Bitmap generateMapPreview() {

		Bitmap res = Bitmap.createBitmap(width, height, Config.RGB_565);

		int[] pixels = new int[width * height];

		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {

				// int[] tiles = TileLib.getTiles(map.mapTiles[x + y *
				// map.width]);

				int color = TileLib.getMegaTileColor(mapTiles[x + y * width]);

				pixels[x + y * width] = color;
			}

		res.setPixels(pixels, 0, width, 0, 0, width, height);

		return res;
	}

	public void draw(int x1, int y1, int x2, int y2) {

	}
}
