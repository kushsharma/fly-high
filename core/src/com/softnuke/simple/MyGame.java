package com.softnuke.simple;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MyGame extends ApplicationAdapter implements InputProcessor{
	
	SpriteBatch batch;
	OrthographicCamera camera;
	OrthographicCamera cameraUi;
	Texture playerT, wallUpT, wallDownT;
	Sprite player, wallUp, wallDown;
	
	Rectangle playerRect;
	Vector2 playerVelocity, playerPosition;
	Array<Rectangle> walls;
	Array<Rectangle> wallsD;
	
	final float WIDTH = 20;
	final float HEIGHT = 15;
	final float GRAVITY = -0.6f;
	final float JUMP_IMPULSE = 9f;
	float SPEED = 10f;
	
	int SCORE = 0;
	
	BitmapFont font;
	Sound hit;
	Music music;
	
	ParticleEffect fire;
	
	ConeLight light;
	PointLight pLight;
	RayHandler rayHandler;
	World world;
	
	Vector3 mouse;
	
	@Override
	public void create () {
		
		//camera for game
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		camera.position.set(WIDTH/2	, HEIGHT/2, 0);
		
		//camera for ui
		cameraUi = new OrthographicCamera();
		cameraUi.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cameraUi.position.set(Gdx.graphics.getWidth()/2	, Gdx.graphics.getHeight()/2, 0);
		
		batch = new SpriteBatch();
		walls = new Array<Rectangle>();
		wallsD  = new Array<Rectangle>();
		
		Gdx.input.setInputProcessor(this);
		
		//fetching textures to memory
		playerT = new Texture("character-ship.png");
		playerT.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		wallUpT = new Texture("wall-up.png");
		wallUpT.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		wallDownT = new Texture("wall-down.png");
		wallDownT.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		//player
		player = new Sprite(playerT);
		player.setSize(HEIGHT/5, HEIGHT/5 * player.getHeight()/player.getWidth());
		player.setPosition(0, HEIGHT/2);
		
		//walls
		wallUp = new Sprite(wallUpT);
		wallUp.setSize(HEIGHT/2 * wallUp.getWidth()/wallUp.getHeight(), HEIGHT/2);
		
		wallDown = new Sprite(wallDownT);
		wallDown.setSize(HEIGHT/2 * wallDown.getWidth()/wallDown.getHeight(), HEIGHT/2);
		
		//rectangle for checking player bounds | used for collision
		playerRect = new Rectangle();		
		playerRect.width = player.getWidth() - player.getWidth()/4;
		playerRect.height = player.getHeight()- player.getHeight()/4;
		playerRect.x = 0;
		playerRect.y = HEIGHT/2;
		
		playerVelocity = new Vector2(SPEED, JUMP_IMPULSE);
		playerPosition = new Vector2(playerRect.x, playerRect.y);
		
		
		font = new BitmapFont(Gdx.files.internal("arial.fnt"));
		font.setUseIntegerPositions(true);
		
		//sound and music
		hit = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
		music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		music.setLooping(true);
		music.play();
		
		//afterburner init
		fire = new ParticleEffect();
		fire.load(Gdx.files.internal("fire.p"), Gdx.files.internal(""));
		fire.start();
		fire.scaleEffect(0.025f);
		
		//world = new World(new Vector2(0,0), true);
		//rayHandler= new RayHandler(world, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		//rayHandler.setCulling(true);
		//rayHandler.setAmbientLight(0.2f);
		
		//pLight = new PointLight(rayHandler, 100, Color.WHITE, WIDTH, WIDTH, HEIGHT/2);
		//pLight.setStaticLight(true);
		//pLight.setSoft(true);
		
		//light = new ConeLight(rayHandler, 100, Color.WHITE, WIDTH, playerPosition.x, playerPosition.y, 360, 45);
		//light.setSoft(true);
		//light.setSoftnessLength(5);
		//light.setStaticLight(true);
		
		//mouse pos
		mouse = new Vector3();
		
		//reset walls position
		reset();
		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.1f, 0.2f, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//update loop
		update(Gdx.graphics.getDeltaTime());
		
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		fire.draw(batch,Gdx.graphics.getDeltaTime());
		player.draw(batch);
		
		for(Rectangle r:walls){
			wallUp.setPosition(r.x, r.y);
			wallUp.draw(batch);				
		}
		
		for(Rectangle r:wallsD){
			wallDown.setPosition(r.x, r.y);
			wallDown.draw(batch);
		}
		
		batch.end();
		
    	//rayHandler.setCombinedMatrix(camera.combined, camera.position.x, camera.position.y, WIDTH, HEIGHT);
		//rayHandler.updateAndRender();
		//world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		
		//render UI
		batch.setProjectionMatrix(cameraUi.combined);
		batch.begin();
		font.draw(batch, String.valueOf(SCORE), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		batch.end();
	}
	
	private void update(float delta){
		
		//add gravity
		playerVelocity.add(0, GRAVITY);
		
		//multiply velocity with delta then add to position
		playerPosition.mulAdd(playerVelocity, delta);		
		
		//update player position
		playerRect.x = playerPosition.x;
		playerRect.y = playerPosition.y;		
		player.setPosition(playerRect.x, playerRect.y);
		
		//afterburner
		fire.setPosition(playerPosition.x + player.getWidth()/4, playerPosition.y + player.getHeight()/2);
		
		//head light
		//light.setPosition(playerPosition.x + player.getWidth()*0.8f, playerPosition.y + player.getHeight()*0.4f);
		//pLight.setPosition(playerPosition.x -WIDTH/2, HEIGHT/2);
		
		//update camera postion
		camera.position.set(playerRect.x + WIDTH/4 + WIDTH/6, HEIGHT/2, 0);
		
		//check player off screens
		if(playerPosition.y < 0 || playerPosition.y > HEIGHT)
		{
			hit.play();
			reset();
		}
		
		//check collisions
		for(Rectangle r:walls){
			if(playerRect.overlaps(r))
			{
				hit.play();
				reset();
			}
		}
		for(Rectangle r:wallsD){
			if(playerRect.overlaps(r))
			{
				hit.play();
				reset();
			}
		}
		
		//System.out.println(Gdx.graphics.getFramesPerSecond());
			
	}
	
	/** reset game **/
	private void reset(){
		
		//reset score and speed
		SCORE = 0;
		SPEED = 10;
		
		//reset player
		playerVelocity.set(SPEED, JUMP_IMPULSE);
		playerPosition.set(0, HEIGHT/2);
		
		//clearing if any old data
		walls.clear();
		wallsD.clear();
		
		//algorithm to generate procedurally random walls
		float start = WIDTH-WIDTH/4;
		for(int i=0;i<50;i++){
			Rectangle wU = new Rectangle();
			Rectangle wD = new Rectangle();
			
			//update x axis
			wU.x = start;
			wD.x = start;
			
			//gap b/w walls
			float gap = HEIGHT/4;
			
			//update y axis
			wD.y = HEIGHT/4 + MathUtils.random(0, HEIGHT/3);
			wU.y = wD.y + gap;
			
			//set heights of rectangle
			wU.height = wallUp.getHeight();
			wD.height = wallDown.getHeight();
			
			walls.add(wU);
			
			//subtract height from rectangle
			wD.y -= wD.height;
			wallsD.add(wD);
			
			start += WIDTH/2 + MathUtils.random(0, 5);
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {

		if(keycode == Keys.R)
		{
			reset();
			return true;
		}

		if(keycode == Keys.ESCAPE)
		{
			Gdx.app.exit();
			return true;
		}

		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		//push plane up a little
		playerVelocity.set(SPEED, JUMP_IMPULSE);

		SCORE++;
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		
		//update difficulty
		if(SCORE>1 && SCORE%5 == 0)
		{
			SPEED++;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mouse.set(screenX, screenY, 0);
		camera.unproject(mouse);
		
		//float dir = MathUtils.atan2(mouse.y - playerPosition.y, mouse.x - playerPosition.x);
		//light.setDirection(MathUtils.radiansToDegrees * dir);
		
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
