package com.gguy.game.estados;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.gguy.game.gamestuff.Guy;
import com.gguy.game.gamestuff.obstaculos.WalkPlatform;

/**
 * Created by Jonas on 01-05-2016.
 */
public class EstadoJogo extends EstadoBase {
    private Guy gguy;
    private Texture fundo; //todo isto é temp
    private float timePassed = 0;
    private int nObstaculos = 6;
   // private Array<Muro> obstaculos;
    private Array<WalkPlatform> walkPlats;
    private final static String TAG = "infoMessage";

    protected EstadoJogo(EstadosManager emg) {
        super(emg);
        fundo = new Texture("fundo1.png");
        gguy = new Guy(50,HEIGHT/2);
        camara.setToOrtho(false, WIDTH/2, HEIGHT/2);
        camara.position.y = HEIGHT/2;
        walkPlats = new Array<WalkPlatform>();
        for(int i = 0;i< nObstaculos;i++){
            walkPlats.add(new WalkPlatform(0));
            walkPlats.get(i).reposition(i*walkPlats.get(i).PLATF_WIDTH);
        }

    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched() && !gguy.isGuyFlying()){
           gguy.changeGravity();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        gguy.updatePos(dt);
        timePassed += dt;
        camara.position.x = gguy.getPosicao().x;

        for(WalkPlatform obstaculo : walkPlats){
            if(camara.position.x - (camara.viewportWidth/2) > obstaculo.getPartCima().x + obstaculo.PLATF_WIDTH)
                obstaculo.reposition(obstaculo.getPartCima().x + obstaculo.PLATF_WIDTH*nObstaculos);
            float fix = obstaculo.ColideGuy(gguy.getColisaoBox());
            if(fix != 0){
                gguy.atingeChao(); //todo isto está buggy!!
               // gguy.fixPosY(fix);
                Logger banana = new Logger(TAG,Logger.INFO);
                String cenas = "colidiu " + gguy.getColisaoBox().y + " com " + obstaculo.getPartBaixo().y;
                banana.info(cenas);
            }
        }
        camara.update();
    }

    @Override
    public void render(SpriteBatch spriteB) {
        spriteB.setProjectionMatrix(camara.combined);
        spriteB.begin();
        spriteB.draw(fundo,camara.position.x - (camara.viewportWidth/2),camara.position.y - (camara.viewportHeight/2));
        if(gguy.isGuyFlying())spriteB.draw(gguy.getJumpAnimation().getKeyFrame(timePassed, true),gguy.getPosicao().x,gguy.getPosicao().y,50,50);
        else if(!gguy.normalGravity())spriteB.draw(gguy.getWalkAnimation().getKeyFrame(timePassed, true),gguy.getPosicao().x,gguy.getPosicao().y,50,50);
        else spriteB.draw(gguy.getInverseWalkAnimation().getKeyFrame(timePassed, true),gguy.getPosicao().x,gguy.getPosicao().y,50,50);
        for(WalkPlatform obstaculo : walkPlats){
            spriteB.draw(obstaculo.getPlatf(),obstaculo.getPartCima().x,obstaculo.getPartCima().y);
            spriteB.draw(obstaculo.getPlatf(),obstaculo.getPartBaixo().x,obstaculo.getPartBaixo().y);
        }

        spriteB.end();
    }

    @Override
    public void freeMemory() {
        fundo.dispose();
        gguy.freeMemory();

        for(WalkPlatform obstaculo : walkPlats){
            obstaculo.freeMemory();
        }
    }
}