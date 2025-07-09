package com.AngryPigeons.storage;

import com.AngryPigeons.domain.Killable;
import com.AngryPigeons.views.LevelScreen;

import java.util.ArrayList;
import java.util.List;

public class SavedLevel {
    private boolean isComplete; // has level been won at least once?

    private int birdPointer;
    private int score;

    private List<SavedKillable> savedPigList;
    private List<SavedKillable> savedMaterialList;

    // default constructor needed for GSON
    public SavedLevel() {
        this.savedMaterialList = new ArrayList<>();
        this.savedPigList = new ArrayList<>();

        this.birdPointer = 0;
        this.score = 0;
    }

    public void save(LevelScreen levelScreen) {
        this.birdPointer = levelScreen.getBirdManager().getBirdPointer();
        this.score = levelScreen.getScore();
        syncIn(savedMaterialList, levelScreen.getEntityManager().getMaterials());
        syncIn(savedPigList, levelScreen.getEntityManager().getPigs());
    }

    private void syncIn(List<SavedKillable> savedKillableList,  List<? extends Killable> killableList) {
        savedKillableList.clear();

        for (Killable killable : killableList) {
            SavedKillable savedKillable = new SavedKillable();
            savedKillable.save(killable);
            savedKillableList.add(savedKillable);
        }
    }

    private void syncOut(List<? extends Killable> killableList, List<SavedKillable> savedKillableList) {

        for(int i = 0; i < killableList.size(); i++) {
            Killable killable = killableList.get(i);
            SavedKillable savedKillable = savedKillableList.get(i);
            savedKillable.load(killable);
        }

    }

    public void load(LevelScreen levelScreen) {
        levelScreen.getBirdManager().setBirdPointer(birdPointer);
        levelScreen.setScore(score);

        syncOut(levelScreen.getEntityManager().getMaterials(), savedMaterialList);
        syncOut(levelScreen.getEntityManager().getPigs(), savedPigList);
    }

    public boolean isComplete() {
        return isComplete;
    }
}
