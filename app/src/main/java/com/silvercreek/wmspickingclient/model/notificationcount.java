package com.silvercreek.wmspickingclient.model;

public class notificationcount {
    private int PickTask;
    private int ReceiveTask;
    private int PhysicalCount;
    private int MoveTask;
    private int LoadPickPallets;

    public int getPickTask() { return PickTask;}
    public void setPickTask(int PickTask) { this.PickTask = PickTask; }
    public int getReceiveTask() {
        return ReceiveTask;
    }
    public void setReceiveTask(int ReceiveTask) {
        this.ReceiveTask = ReceiveTask;
    }
    public int getPhysicalCount() {
        return PhysicalCount;
    }
    public void setPhysicalCount(int PhysicalCount) {
        this.PhysicalCount = PhysicalCount;
    }
    public int getMoveTask() {
        return MoveTask;
    }
    public void setMoveTask(int MoveTask) {
        this.MoveTask = MoveTask;
    }
    public int getLoadPickPallets() {
        return LoadPickPallets;
    }
    public void setLoadPickPallets(int LoadPickPallets) {
        this.LoadPickPallets = LoadPickPallets;
    }
}
