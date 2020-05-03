package com.helion3.prism.listeners;

import com.helion3.prism.Prism;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;

public class ChangeSignListener {

  /**
   * Listens to the base change block event.
   *
   * @param event ChangeBlockEvent
   */
  @Listener(order = Order.POST)
  public void onChangeSign(ChangeSignEvent event) {
    if (event.getCause().first(Player.class).map(Player::getUniqueId).map(Prism.getInstance().getActiveWands()::contains).orElse(false)) {
      // Cancel and exit event here, not supposed to place/track a block with an active wand.
      event.setCancelled(true);
      return;
    }

    if (!Prism.getInstance().getConfig().getEventCategory().isSignChange()) {
      return;
    }
    Sign tileEntity = event.getTargetTile();

    // TODO implement

    // Get relevant data from this event -- namely, the changed sign data
    SignData originalSignData = tileEntity.getSignData();
    SignData finalSignData = event.getText();

    // Create prism record
//    PrismRecord.create()
//        .source(event.getCause())
//        .signOriginal(originalSignData)
//        .signReplacement(finalSignData)
//        .location(tileEntity.getLocation())
//        .event(PrismEvents.SIGN_CHANGE)
//        .target(tileEntity.getBlock().getId().replace("_", " "))
//        .buildAndSave();
  }
}
