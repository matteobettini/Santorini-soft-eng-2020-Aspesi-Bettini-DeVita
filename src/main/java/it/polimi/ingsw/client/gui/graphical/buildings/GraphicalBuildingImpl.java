package it.polimi.ingsw.client.gui.graphical.buildings;

import it.polimi.ingsw.client.gui.utils.STLImportInfo;
import it.polimi.ingsw.client.gui.utils.STLImporter;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;

/**
 * Common class for a 3D building.
 * Contains link to container Group, and manages its graphical node.
 */
abstract class GraphicalBuildingImpl {

    private MeshView myGraphics;
    private Group container;

    private PhongMaterial originalMaterial;
    private PhongMaterial selectedMaterial;

    private STLImporter stlImporter = STLImporter.getImporter();

    GraphicalBuildingImpl(Group container, Point3D position, Color originalColor, Color selectedColor, String meshPath) {
        assert container != null && position != null && originalColor != null && selectedColor != null && meshPath != null;
        this.container = container;
        initGraphics(meshPath, position, originalColor, selectedColor);
    }

    /**
     * Load 3D graphics and add to the container to start rendering
     * @param meshPath Path of Mesh object
     * @param position 3D position where to place
     * @param originalColor Color when the object is not selected
     * @param selectedColor Color when the object is selected
     */
    private void initGraphics(String meshPath, Point3D position, Color originalColor, Color selectedColor){
        //Load textures
        originalMaterial = new PhongMaterial();
        originalMaterial.setDiffuseColor(originalColor);
        selectedMaterial = new PhongMaterial();
        selectedMaterial.setDiffuseColor(selectedColor);

        //Load mesh and add to container
        STLImportInfo importInfo = new STLImportInfo(position);
        myGraphics = stlImporter.importMesh(meshPath,originalMaterial,importInfo);
        assert myGraphics != null;
        container.getChildren().add(myGraphics);
    }

    /**
     * Change selection status of this object according to isSelected flag
     * @param isSelected True if selected
     */
    public void setSelected(boolean isSelected) {
        if (isSelected)
            myGraphics.setMaterial(selectedMaterial);
        else
            myGraphics.setMaterial(originalMaterial);
    }

    /**
     * Remove this building from graphics
     */
    public void remove() {
        container.getChildren().remove(myGraphics);
    }
}
