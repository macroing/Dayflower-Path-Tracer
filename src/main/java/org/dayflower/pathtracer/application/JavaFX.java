/**
 * Copyright 2009 - 2017 J&#246;rgen Lundgren
 * 
 * This file is part of Dayflower.
 * 
 * Dayflower is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Dayflower is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dayflower. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dayflower.pathtracer.application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Region;

public final class JavaFX {
	private JavaFX() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static CheckMenuItem newCheckMenuItem(final String text, final EventHandler<ActionEvent> eventHandler) {
		return newCheckMenuItem(text, eventHandler, false);
	}
	
	public static CheckMenuItem newCheckMenuItem(final String text, final EventHandler<ActionEvent> eventHandler, final boolean isSelected) {
		final
		CheckMenuItem checkMenuItem = new CheckMenuItem(text);
		checkMenuItem.setOnAction(eventHandler);
		checkMenuItem.setSelected(isSelected);
		
		return checkMenuItem;
	}
	
	public static Menu newMenu(final String text, final MenuItem... menuItems) {
		final
		Menu menu = new Menu(text);
		menu.getItems().addAll(menuItems);
		
		return menu;
	}
	
	public static MenuItem newMenuItem(final String text, final EventHandler<ActionEvent> eventHandler) {
		final
		MenuItem menuItem = new MenuItem(text);
		menuItem.setOnAction(eventHandler);
		
		return menuItem;
	}
	
	public static RadioMenuItem newRadioMenuItem(final String text, final EventHandler<ActionEvent> eventHandler, final boolean isSelected, final ToggleGroup toggleGroup) {
		final
		RadioMenuItem radioMenuItem = new RadioMenuItem(text);
		radioMenuItem.setOnAction(eventHandler);
		radioMenuItem.setToggleGroup(toggleGroup);
		radioMenuItem.setSelected(isSelected);
		
		return radioMenuItem;
	}
	
	public static Region newRegion(final double top, final double right, final double bottom, final double left) {
		final
		Region region = new Region();
		region.setPadding(new Insets(top, right, bottom, left));
		
		return region;
	}
}