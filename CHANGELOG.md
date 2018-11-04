# 更新日志

## 0.3.1

 - Drink is now nameable
 - Optimize renderers
 - Fix emptying juice bottle returning malformed item
 - Fix basic juice not curing thirst
 - Fix cascading worldgen
 - Fix bamboo destroying solid block while growing
 - Hopefully fix Drinkro™ looking strange with Optifine
 - Add Forestry compat

## 0.3.0

 - Now **requires Forge 14.23.5.2772** to run due to technical reasons.
 - Add Drink.
   - It is just a specialized dish.
   - Yes, you drink it, not eat it.
   - Being similar to dish, drink also supports arbitrary ingredients and "seasonings".
   - Notice that it is not soup.
 - Some materials can exist in form of "juice" now.
 - Add "Drinkro™"
   - Uh. It makes drinks. That's it.
   - Right click it with valid ingredients to put them in.
   - Highly WIP. Report bugs if you encounter one.
 - Add Basins.
   - Basins are processing devices.
   - They come with two variants: wooden and earthen. Earthen basin are made from clay.
   - Earthen variants may be dyed.
   - May be used for distillation. For example, salt now requires basin to make. See JEI for details.
     - Distillation requires heat source. Sunlight may work if enabled in config. Typical heat source include lava and torches.
     - Old way of making salt are removed.
   - May be used for throwing things into it to get new things. See JEI for details.
   - Has a basic CraftTweaker support.
 - Add Squeezer.
   - It is essentially a miniature vanilla piston.
   - It has animation!
   - You should use it together with basin.
   - Does not exist in item form; you may get it by putting a vanilla piston on top of a basin.
   - Use redstone to control it, just like a vanilla piston.
   - Used for squeezing stuff in basin. See JEI for details.
   - Has a basic CraftTweaker support.
 - Add several fruits that belongs to Citrus genus.
   - Namely: Pomelo, Citron, Mandarin, Grapefruit, Orange, Lemon and Lime.
   - Their plants are trees. So there are corresponding wood logs, saplings and leaves.
   - Of course you can use those wood logs to make chopping boards.
   - However, do notice that those saplings are still WIP, and they cannot grow right now.
   - Saplings and logs are currently unobtainable (unless using JEI or some other sorts).
 - Chopping Board now has a better model.
   - The word "better" here is referring the technical details.
     - Its visual appearance does not change at all.
     - The item form of chopping board is no longer a `TileEntityItemStackRenderer`.
     - The block form of chopping board is no longer using `TileEntitySpecialRenderer` for the board base part and the knife part.
     - The TESR is kept for rendering the item being put on board.
   - May improve both performance and compatibility. According to reports, they work fine with OptiFine now.
   - Many thanks to @tterrag1098, @bs2609, @Shadow-of-Fire and @gigaherz for their help on this one!
 - Bamboo now has a better model.
 - Bamboo may be used as a blowpipe now. This can be disabled in config.
 - Add a vast catalog of inter-mod compatibilities.
   - You may use JEI to look up all three types of basin processing recipes.
   - You may use CraftTweaker to add more basin processing recipes. Recipe removal will be here soon™.
   - Add HWYLA support. Modelled after the existed TheOneProbe support.
   - Add Farming for Blockheads support. You may get Cuisine's bamboo shoots from that.
   - Add ToughAsNail support. Drinks may restore thirst value if TAN is present.
   - Certain raw food and liquids from the following are now valid ingredients or seasonings in Cuisine:
     - Biomes O' Plenty
     - Pam's HarvestCraft
     - PizzaCraft
     - Rustic
     - Vanilla Food Pantry
 - Address [GH-12](https://github.com/Snownee/Cuisine/issues/12): there is now a config option to not giving new player a Culinary Manual.
 - Fix [GH-15](https://github.com/Snownee/Cuisine/issues/15): crash on dedicated server due to accidentally referring client-only class.
 - API overhaul.
   - There are now methods to allow other mods to map their items to certain Cuisine ingredients.
   - `isKnownMaterial` -> `isKnownIngredient`
   - Introduce `CompositeFood.Builder`. It represents a "WIP" dish.
   - `CompositeFood` is now fully abstract. You may extend this to have a brand new type of compound food.
     - As an example, the new drink system is built on this.
   - `IngredientCharacteristics` -> `IngredientTrait`
   - New enum: `Taste`. Has 5 values: `SOUR`, `SWEET`, `BITTER`, `SALTY` and `SAVOR`. Not used so far.
   - `CookingStrategy` gets overhauled.
   - Many other breaking changes - after all, we are still on `0.x.x` stage. Everything is in flux.
 - Fix many bugs. We don't even remember what they are, or those bugs only ever existed in dev environment.
   - May introduce more bugs. You have been warned.
 - Many internal code cleanup. Under 90% circumstances they do NOT improve performance; they are mainly for code style and easier development.

## 0.2.4

 - Add item of blocks that make from fire pit
 - Change JEI information to tooltip description
 - Auto-disable Hardcore lower food value if AppleCore installed
 - Fix [GH-4](https://github.com/Snownee/Cuisine/issues/4): Crash when placing mortar in crafting grid (thanks to xAlicatt)

## 0.2.3

 - Fix [GH-1](https://github.com/Snownee/Cuisine/issues/1): Start-up crash due to Chopping Board
 - Fix server-side crash caused by jar (thanks to 见习都督)

## 0.2.2

 - 新增两个技能
 - 修复水稻掉落条件错误
 - 修复bug

## 0.2.1

 - 前置模组现在独立出来了
 - 修复部分效果不显示
 - 修复部分效果颜色渲染错误
 - 修复麻辣效果条件错误
 - 修复砧板配方显示错误
 - 修复技能的解锁条件错误
 - 修复附魔金苹果食材缺少翻译
 - 技能升级时添加音效

## 0.2.0

 - 新增菜品命名功能
 - 新增硬核模式（默认开启）
 - 重写食材获取方式，移除葱郁草丛
 - 新增技能系统，本次更新加入了两个技能
 - 现在可以烹饪河豚了
 - 新增一些实用的发射器行为
 - 新增 FarmingForBlockheads 支持
 - 新增矿物词典支持
 - 砧板伐木输出 4 -> 6
 - 优化文字渲染与排版
 - 效果抵抗状态现在不能被牛奶消除
 - 作物支持了大部分光影的动态植物功能
 - 附魔金苹果的食材会发出光芒了
 - 食材数值微调

## 0.1.1

 - 修复多人模式下菜品消失
 - 修复非 Unicode 字符不渲染
 - 修复竹子生成过多（感谢 送葬豆酱）

## 0.1.0

 - 重写菜品食用机制
 - ~~新增 Steve's Carts Reborn 支持~~
 - 新增调料的矿物词典支持
 - 新增菜品清空配方（感谢 Dior Mayway）
 - 葱郁草丛可用剪刀精准采集（感谢 柑橘大叔)
 - 磨不再自动输入发射器
 - 减少臼的饱食消耗
 - 修复菜品饱腹度显示错误
 - 修复两格作物在耕地被踩坏后不掉落
 - 修复罐子输出流体错误（感谢 磷叶石）
 - 修复 JEI 出现无效臼配方
 - 修复模组名在搜索标签页中被图标遮挡

## 0.0.9b

 - 新增辣椒食用效果
 - 修复调料瓶发起烹饪崩溃
 - 修复空手炒菜
 - 修复 CrT 无法移除自带配方
 - 生命精华的粒子好看了一点

## 0.0.9a

 - 修复竹子未出现在标签页中
 - 修复未删除的调试代码

## 0.0.9

 - 重写罐子加工配方
 - 罐子加工JEI、CrT支持
 - 系统优化
 - 菜品现在能在生存模式下放置和食用了
 - 修复调料翻译缺失
 - 修复甜菜无法被生命精华催熟
 - 修复区块重新加载后锅内食材不可见
 - 修复玩家无法在竹子上疾跑
 - 新增超多 bugs

## 0.0.8a

 - 修复竹子可在沙漠和雪地蔓延
 - 修复砧板加工食材导致材质缺失

## 0.0.8

 - 新增竹子、竹笋、竹炭
 - 稀有掉落的食材显示彩色名称
 - 稀有掉落的食材种植时加速生长
 - 新增稍便宜的盘子配方
 - 修复切菜时食材由丁切为丝
 - 修复臼加工部分食材失效

## 0.0.7

 - 按住 Shift 时可显示菜品剩余比例
 - 修复 Nutrition 联动失效
 - 修复使用磨崩溃
 - 修复调料瓶配方物品复制
 - 修复区块重新加载后锅内食材不可见

## 0.0.6

 - 增加食材对矿物词典的支持
 - 食材可堆叠
 - 食材可食用
 - 增加了调料瓶使用时的效果
 - 调料瓶可在注册牛奶流体时挤奶
 - 调料瓶可通过合成存入物品调料
 - 补全了英文翻译
 - 增加JEI描述
 - 添加某些机器的红石比较器支持
 - 更新青红椒材质
 - 修复状态图标渲染
 - 修复手册按钮在特殊比例下位置错误
 - 修复磨交互不同步
 - 修复砧板物品变暗

## 0.0.5

 - 为村民添加相关交易
 - 辣椒现在需要种植在灵魂沙上
 - 修正了一些食材的命名
 - 臼可以与储罐交互了
 - 模组的配方会自动在配方书中解锁
 - 添加某些机器的红石比较器支持
 - 修复臼无法正常装水的问题
 - 修复作物种植在BWM培养盆中崩溃的问题
 - 修复破坏臼方块不掉落的问题
 - 修复食材状态在砧板重置的问题
 - 修复生命精华配方失效
 - 修复合成面团返还物品错误
 - 修复多人游戏中其他玩家无法看到锅内食材
 - 修复多人模式中其他玩家臼和磨的动画
 - 修复罐子配方处理
 - 修复磨吞物品
 - 修复打草掉落错误物品
