package com.silvercreek.wmspickingclient.model;

public class menulist {
    private int PickTask;
    private int LoadPickPallets;
    private int MoveTask;
    private int MoveManually;
    private int PhysicalCount;
    private int ReceiveTask;
    private int BreakerUomUtility;

    public int getPickTask() { return PickTask;}
    public void setPickTask(int PickTask) { this.PickTask = PickTask; }
    public int getLoadPickPallets() {
        return LoadPickPallets;
    }
    public void setLoadPickPallets(int LoadPickPallets) {
        this.LoadPickPallets = LoadPickPallets;
    }
    public int getMoveTask() {
        return MoveTask;
    }
    public void setMoveTask(int MoveTask) {
        this.MoveTask = MoveTask;
    }
    public int getMoveManually() {
        return MoveManually;
    }
    public void setMoveManually(int MoveManually) {
        this.MoveManually = MoveManually;
    }
    public int getPhysicalCount() {
        return PhysicalCount;
    }
    public void setPhysicalCount(int PhysicalCount) {
        this.PhysicalCount = PhysicalCount;
    }
    public int getReceiveTask() {
        return ReceiveTask;
    }
    public void setReceiveTask(int ReceiveTask) {
        this.ReceiveTask = ReceiveTask;
    }
    public int getBreakerUomUtility() {
        return BreakerUomUtility;
    }
    public void setBreakerUomUtility(int BreakerUomUtility) {
        this.BreakerUomUtility = BreakerUomUtility;
    }
}
