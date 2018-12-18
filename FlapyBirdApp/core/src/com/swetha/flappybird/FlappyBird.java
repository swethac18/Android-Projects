package com.swetha.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	BitmapFont font;
	SpriteBatch batch;
	int flapState=0;
	Texture backGround;
	Texture[] birds;
	int gameState=0;
	float gravity=2;
	float velocity=0;
	float birdY=0;
	Texture bottomTube;
	Texture topTube;
	int gap=400;
	float maxTubeOffset=0;
	Random randomNumber;
	float tubeVelocity=4;
	int numOfTubes=4;
	float[] tubeX= new float[numOfTubes];
	float[] tubeOffset = new float[numOfTubes];
	float distanceBetweenTubes;
	Circle birdCircle;
//	ShapeRenderer shapeRenderer;
	Rectangle[] topTubeRectangle;
	Rectangle[] bottomTubeRectangle;
	int score=0;
	int scoringTube=0;
	Texture gameOver;

	
	@Override
	public void create () {
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(15);
		batch = new SpriteBatch();
		backGround = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		bottomTube = new Texture("bottomtube.png");
		topTube = new Texture("toptube.png");
		maxTubeOffset = Gdx.graphics.getHeight()/2-gap/2-100;
		randomNumber = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth()*3/4;
	//	shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		topTubeRectangle = new Rectangle[numOfTubes];
		bottomTubeRectangle = new Rectangle[numOfTubes];
		startGame();
		gameOver = new Texture("gameover.png");

	}

	public void startGame(){
		birdY=Gdx.graphics.getHeight()/2-birds[0].getHeight()/2;
		for(int i=0;i<numOfTubes;i++){
			tubeOffset[i] = (randomNumber.nextFloat() -0.5f) * (Gdx.graphics.getHeight()/2 - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth()/2-bottomTube.getWidth()/2 + Gdx.graphics.getWidth()+i * distanceBetweenTubes;
			topTubeRectangle[i] = new Rectangle();
			bottomTubeRectangle[i] = new Rectangle();
		}
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(backGround,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState == 1){

			if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2){
				score++;
				Gdx.app.log("Score",String.valueOf(score));
				if(scoringTube < numOfTubes-1)
					scoringTube++;
				else
					scoringTube=0;
			}

			if(Gdx.input.justTouched()){
				velocity = -30;
			}
			for(int i=0;i<numOfTubes;i++) {
				if(tubeX[i] < -topTube.getWidth() ){
					tubeX[i] += numOfTubes*distanceBetweenTubes;
					tubeOffset[i] = (randomNumber.nextFloat() -0.5f) * (Gdx.graphics.getHeight()/2 - gap - 200);
				}
				else{
					tubeX[i] -= tubeVelocity;
				}

				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				topTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
				bottomTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());
			}

			if(birdY > 0){
				velocity +=gravity;
				birdY -= velocity;
			}
			else{
				gameState = 2;
			}
		}
		else if(gameState == 0){
			if(Gdx.input.justTouched())
				gameState=1;
		}
		else if(gameState == 2){
			batch.draw(gameOver,Gdx.graphics.getWidth()/2-gameOver.getWidth()/2,Gdx.graphics.getHeight()/2-gameOver.getHeight()/2);
			if(Gdx.input.justTouched()) {
				gameState = 1;
				startGame();
				scoringTube = 0;
				score = 0;
				velocity = 0;
			}

		}

		if(flapState == 0){
			flapState=1;
		}
		else{
			flapState=0;
		}

		batch.draw(birds[flapState],Gdx.graphics.getWidth()/2-birds[flapState].getWidth()/2,birdY);
		font.draw(batch,String.valueOf(score),100,200);
		batch.end();
		birdCircle.set(Gdx.graphics.getWidth()/2,birdY+birds[flapState].getHeight()/2,birds[flapState].getWidth()/2);
	/*	shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius); */
		for(int i=0;i<numOfTubes;i++) {
		//	shapeRenderer.rect(topTubeRectangle[i].x, topTubeRectangle[i].y, topTubeRectangle[i].width, topTubeRectangle[i].height);
		//	shapeRenderer.rect(bottomTubeRectangle[i].x, bottomTubeRectangle[i].y, bottomTubeRectangle[i].width, bottomTubeRectangle[i].height);

			//check for collisions
			if(Intersector.overlaps(birdCircle,topTubeRectangle[i]) || Intersector.overlaps(birdCircle,bottomTubeRectangle[i])){
			//	Gdx.app.log("collision","game must over");
				gameState = 2;
			}
		}
	//	shapeRenderer.end();
	}
	
	@Override
	public void dispose () {

	}
}
