package hotheart.starcraft.configure;

public class BuildParameters {

	// Removes some debug information
	public static final boolean DEBUG = false;

	// Debug iScript engine and writes message if opcode not implemented
	public static final boolean DEBUG_OPCODE = false;

	// Displaying errors in GRP rendering
	public static final boolean DEBUG_GRP_RENDER_ERROR = true;

	// Cache GRP as Bitmap
	public static final boolean CACHE_GRP = false;

	// Playing sounds. If there no sounds in /starcraft/sounds folder and
	// PLAY_SOUNDS = true then game will run slower
	public static final boolean PLAY_SOUNDS = true;
	
	public static final String GAME_FOLDER = "/sdcard/starcraft/";
}