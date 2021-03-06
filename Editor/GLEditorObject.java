package Editor;

import openGL.GLObject;
import ToolBox.Matrix;

/**
 * Created by andri on 02-Aug-16.
 */

public class GLEditorObject extends GLObject {

    public static final float hW = 720 / 1280f;

    private float[] mvMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
    };
    private float[] pMatrix = mvMatrix.clone(), scaleMoveM = mvMatrix.clone(),
            pointRotationM = mvMatrix.clone(), tempM = mvMatrix.clone(),
            RotationM = mvMatrix.clone(), xRotationM = mvMatrix.clone(),
            yRotationM = mvMatrix.clone(), zRotationM = mvMatrix.clone(),
            cameraPrM = mvMatrix.clone(), relativeRotation = mvMatrix.clone();


    private float[] angle = new float[3];
    private float[] rotationPosition = new float[3];

    public GLEditorObject(GLObjectBuilder builder) {
        super(builder);
        mvMatrix[0] = hW;
    }

    public void updateProjectMatrix() {
        Matrix.multiplyMM(tempM, xRotationM, pointRotationM);
        Matrix.multiplyMM(cameraPrM, yRotationM, tempM);
        Matrix.multiplyMM(RotationM, zRotationM, cameraPrM);
        Matrix.multiplyMM(pMatrix, scaleMoveM, RotationM);
        Matrix.multiplyMM(cameraPrM, Camera.projectionMatrix, pMatrix);
        super.addUniformMatrix(cameraPrM, "u_pMatrix");
    }

    public void rotateXYZ(float[] anglesInDegrees){
        angle[0] += anglesInDegrees[0] * (float)Math.PI/180f;
        angle[1] += anglesInDegrees[1] * (float)Math.PI/180f;
        angle[2] += anglesInDegrees[2] * (float)Math.PI/180f;

        xRotationM[5] = (float) Math.cos(angle[0]);
        xRotationM[6] = (float) -Math.sin(angle[0]);
        xRotationM[9] = (float) Math.sin(angle[0]);
        xRotationM[10] = (float) Math.cos(angle[0]);

        yRotationM[0] = (float) Math.cos(angle[1]);
        yRotationM[2] = (float) Math.sin(angle[1]);
        yRotationM[8] = (float) -Math.sin(angle[1]);
        yRotationM[10] = (float) Math.cos(angle[1]);

        zRotationM[0] = (float) Math.cos(angle[2]);
        zRotationM[1] = (float) -Math.sin(angle[2]);
        zRotationM[4] = (float) Math.sin(angle[2]);
        zRotationM[5] = (float) Math.cos(angle[2]);
    }

    public void rotateAroundXYZ(float[] anglesInDegrees, float[] rotationPoint){

        /*if(rotationPoint.length < 3) return;
        else
            rotationPoint = new float[]{rotationPoint[0], rotationPoint[1], rotationPoint[2], 1.f};
        anglesInDegrees= new float[]{rotationPoint[0], rotationPoint[1], rotationPoint[2], 1.f};
        Matrix.multiplyMV(rotationPoint, rotationPoint.clone(), pointRotationM);
*/
        float []relativeAngle = {
                anglesInDegrees[0] * (float)Math.PI / 180f,
                anglesInDegrees[1] * (float)Math.PI / 180f,
                anglesInDegrees[2] * (float)Math.PI / 180f,};

        float[] transRotationMatrix = {
                1.f, 0.f, 0.f, 0.f,
                0.f, 1.f, 0.f, 0.f,
                0.f, 0.f, 1.f, 0.f,
                0.f, 0.f, 0.f, 1.f,};

        float[] xPointRot = transRotationMatrix.clone();
        float[] yPointRot = transRotationMatrix.clone();
        float[] zPointRot = transRotationMatrix.clone();

        transRotationMatrix[12] = -rotationPoint[0];
        transRotationMatrix[13] = -rotationPoint[1];
        transRotationMatrix[14] = -rotationPoint[2];

        xPointRot[5] = (float) Math.cos(relativeAngle[0]);
        xPointRot[6] = (float) -Math.sin(relativeAngle[0]);
        xPointRot[9] = (float) Math.sin(relativeAngle[0]);
        xPointRot[10] = (float) Math.cos(relativeAngle[0]);

        yPointRot[0] = (float) Math.cos(relativeAngle[1]);
        yPointRot[2] = (float) Math.sin(relativeAngle[1]);
        yPointRot[8] = (float) -Math.sin(relativeAngle[1]);
        yPointRot[10] = (float) Math.cos(relativeAngle[1]);

        zPointRot[0] = (float) Math.cos(relativeAngle[2]);
        zPointRot[1] = (float) -Math.sin(relativeAngle[2]);
        zPointRot[4] = (float) Math.sin(relativeAngle[2]);
        zPointRot[5] = (float) Math.cos(relativeAngle[2]);


        Matrix.multiplyMM(tempM, transRotationMatrix, pointRotationM);
        Matrix.multiplyMM(pointRotationM, xPointRot, tempM);

        transRotationMatrix[12] = rotationPoint[0];
        transRotationMatrix[13] = rotationPoint[1];
        transRotationMatrix[14] = rotationPoint[2];
        //rotationPosition = rotationPoint;

        Matrix.multiplyMM(xPointRot, yPointRot, pointRotationM);
        Matrix.multiplyMM(tempM, zPointRot, xPointRot);
        Matrix.multiplyMM(pointRotationM, transRotationMatrix, tempM);

    }

    public void translate(float[] position){
        scaleMoveM[12] = position[0];
        scaleMoveM[13] = position[1];
        scaleMoveM[14] = position[2];
    }

    public void move(float[] shift){
        scaleMoveM[12] += shift[0];
        scaleMoveM[13] += shift[1];
        scaleMoveM[14] += shift[2];
    }

    public void scale(float[] scale){
        scaleMoveM[0] *= scale[0];
        scaleMoveM[5] *= scale[1];
        scaleMoveM[10] *= scale[2];
    }

    public float[] getRelativePointPosition(float[] pos){
        updateProjectMatrix();
        float[] res = new float[4];
        Matrix.multiplyMV(res, pos, pMatrix);
        return res;
    }

    public float[] getPosition(){ return new float[]{scaleMoveM[12], scaleMoveM[13], scaleMoveM[14]}; }

    public float[] getScale(){return new float[]{scaleMoveM[0], scaleMoveM[5], scaleMoveM[10]};}

    public float[] getRotationAngles(){return angle;}
}
