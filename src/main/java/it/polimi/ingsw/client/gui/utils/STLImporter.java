package it.polimi.ingsw.client.gui.utils;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import it.polimi.ingsw.common.utils.ResourceScanner;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;

import java.net.URL;

public class STLImporter {

    private static STLImporter importer;
    private ResourceScanner scanner = ResourceScanner.getInstance();

    private STLImporter(){
        importer = null;
    }

    public static STLImporter getImporter() {
        if (importer == null)
            importer = new STLImporter();
        return importer;
    }

    public MeshView importMesh(String resourcePath, String texturePath, STLImportInfo importInfo){
        assert texturePath != null;
        //Load mesh
        MeshView meshView = internalImport(resourcePath,importInfo);
        //Load material
        URL path = scanner.getResourcePath(texturePath);
        assert path != null;
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image(path.toString()));
        meshView.setMaterial(material);
        return meshView;
    }
    public MeshView importMesh(String resourcePath, Color color, STLImportInfo importInfo){
        assert color != null;
        //Load mesh
        MeshView meshView = internalImport(resourcePath,importInfo);
        //Load material
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        meshView.setMaterial(material);
        return meshView;
    }
    public MeshView importMesh(String resourcePath, PhongMaterial material, STLImportInfo importInfo){
        assert material != null;
        //Load mesh
        MeshView meshView = internalImport(resourcePath,importInfo);
        //Load material
        meshView.setMaterial(material);
        return meshView;
    }
    private MeshView internalImport(String resourcePath, STLImportInfo importInfo){
        assert resourcePath != null && importInfo != null;
        URL path = scanner.getResourcePath(resourcePath);
        assert path != null;
        StlMeshImporter importer = new StlMeshImporter();
        importer.read(path);
        Mesh mesh = importer.getImport();
        assert mesh != null;
        MeshView meshView = new MeshView(mesh);
        meshView.setScaleX(importInfo.getScale());
        meshView.setScaleY(importInfo.getScale());
        meshView.setScaleZ(importInfo.getScale());
        meshView.setTranslateX(importInfo.getX());
        meshView.setTranslateY(importInfo.getY());
        meshView.setTranslateZ(importInfo.getZ());
        return meshView;
    }
}
