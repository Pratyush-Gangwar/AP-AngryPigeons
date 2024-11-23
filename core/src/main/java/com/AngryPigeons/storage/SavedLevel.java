package com.AngryPigeons.storage;

import com.AngryPigeons.domain.Killable;
import com.AngryPigeons.domain.Material;
import com.AngryPigeons.domain.Pig;
import com.AngryPigeons.views.LevelScreen;

import java.util.ArrayList;
import java.util.List;

public class SavedLevel {
    private boolean isComplete;

    private List<SavedKillable> iceBlocks;
    private List<SavedKillable> woodBlocks;
    private List<SavedKillable> stoneBlocks;

    private int birdPointer;

    private List<SavedKillable> smallPigs;
    private List<SavedKillable> mediumPigs;
    private List<SavedKillable> largePigs;

    private transient LevelScreen levelScreen;

    public SavedLevel(LevelScreen levelScreen) {
        this.levelScreen = levelScreen;

        this.iceBlocks = new ArrayList<>();
        this.woodBlocks = new ArrayList<>();
        this.stoneBlocks = new ArrayList<>();

        this.smallPigs = new ArrayList<>();
        this.mediumPigs = new ArrayList<>();
        this.largePigs = new ArrayList<>();

        sync();
    }

    public void sync() {
        this.isComplete = levelScreen.isComplete();
        this.birdPointer = levelScreen.getBirdPointer();

        syncKillable(iceBlocks, levelScreen.getIceBlocks());
        syncKillable(woodBlocks, levelScreen.getWoodBlocks());
        syncKillable(stoneBlocks, levelScreen.getStoneBlocks());

        syncKillable(smallPigs, levelScreen.getSmallPigs());
        syncKillable(mediumPigs, levelScreen.getMediumPigs());
        syncKillable(largePigs, levelScreen.getLargePigs());
    }

    private void syncKillable(List<SavedKillable> savedKillableList,  List<? extends Killable> killableList) {

        for(int i = 0; i < killableList.size(); i++) {
            Killable killable = killableList.get(i);
            SavedKillable savedKillable;

            if (i < savedKillableList.size()) {
                savedKillable = savedKillableList.get(i);
                savedKillable.sync();
            } else {
                savedKillable = new SavedKillable(killable);
                savedKillableList.add(savedKillable);
            }
        }
    }
}
