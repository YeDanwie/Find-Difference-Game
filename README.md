# 找茬游戏，Java Swing编程

## 功能介绍

1.自带五个关卡，每个关卡有五处不同，限时30秒

2.支持玩家自己导入关卡

3.有随机游戏（在自带关卡里随机选一个）模式和选择关卡模式

## 实现原理

每个关卡包含两张图片和五处不同点的坐标，当玩家点击鼠标时获取点击处的坐标，计算该坐标和答案坐标的距离是否小于某个范围，是则说明玩家找到一个不同处，否则说明查找错误。
