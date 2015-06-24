package net.mobctrl.interfaces;

import net.mobctrl.model.ItemData;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public interface ItemDataClickListener {

	public void onExpandChildren(ItemData itemData);

	public void onHideChildren(ItemData itemData);

}
