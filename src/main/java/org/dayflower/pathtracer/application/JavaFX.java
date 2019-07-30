/**
 * Copyright 2015 - 2019 J&#246;rgen Lundgren
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

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Region;

/**
 * A class that consists exclusively of static methods that operates on or returns JavaFX components.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class JavaFX {
	private JavaFX() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a new {@code CheckBox} instance.
	 * <p>
	 * Calling this method is equivalent to {@code newCheckBox(text, eventHandler, false)}.
	 * 
	 * @param text the value of the property {@code text}
	 * @param eventHandler the value of the property {@code onAction}
	 * @return a new {@code CheckBox} instance
	 */
	public static CheckBox newCheckBox(final String text, final EventHandler<ActionEvent> eventHandler) {
		return newCheckBox(text, eventHandler, false);
	}
	
	/**
	 * Returns a new {@code CheckBox} instance.
	 * 
	 * @param text the value of the property {@code text}
	 * @param eventHandler the value of the property {@code onAction}
	 * @param isSelected the value of the property {@code selected}
	 * @return a new {@code CheckBox} instance
	 */
	public static CheckBox newCheckBox(final String text, final EventHandler<ActionEvent> eventHandler, final boolean isSelected) {
		final
		CheckBox checkBox = new CheckBox();
		checkBox.setOnAction(eventHandler);
		checkBox.setSelected(isSelected);
		checkBox.setText(text);
		
		return checkBox;
	}
	
	/**
	 * Returns a new {@code CheckMenuItem} instance.
	 * <p>
	 * Calling this method is equivalent to {@code newCheckMenuItem(text, eventHandler, false)}.
	 * 
	 * @param text the value of the property {@code text}
	 * @param eventHandler the value of the property {@code onAction}
	 * @return a new {@code CheckMenuItem} instance
	 */
	public static CheckMenuItem newCheckMenuItem(final String text, final EventHandler<ActionEvent> eventHandler) {
		return newCheckMenuItem(text, eventHandler, false);
	}
	
	/**
	 * Returns a new {@code CheckMenuItem} instance.
	 * 
	 * @param text the value of the property {@code text}
	 * @param eventHandler the value of the property {@code onAction}
	 * @param isSelected the value of the property {@code selected}
	 * @return a new {@code CheckMenuItem} instance
	 */
	public static CheckMenuItem newCheckMenuItem(final String text, final EventHandler<ActionEvent> eventHandler, final boolean isSelected) {
		final
		CheckMenuItem checkMenuItem = new CheckMenuItem();
		checkMenuItem.setOnAction(eventHandler);
		checkMenuItem.setSelected(isSelected);
		checkMenuItem.setText(text);
		
		return checkMenuItem;
	}
	
	/**
	 * Returns a new {@code Menu} instance.
	 * 
	 * @param text the value of the property {@code text}
	 * @param menuItems the {@code MenuItem}s to add
	 * @return a new {@code Menu} instance
	 */
	public static Menu newMenu(final String text, final MenuItem... menuItems) {
		final
		Menu menu = new Menu();
		menu.getItems().addAll(menuItems);
		menu.setText(text);
		
		return menu;
	}
	
	/**
	 * Returns a new {@code MenuItem} instance.
	 * 
	 * @param text the value of the property {@code text}
	 * @param eventHandler the value of the property {@code onAction}
	 * @return a new {@code MenuItem} instance
	 */
	public static MenuItem newMenuItem(final String text, final EventHandler<ActionEvent> eventHandler) {
		final
		MenuItem menuItem = new MenuItem();
		menuItem.setOnAction(eventHandler);
		menuItem.setText(text);
		
		return menuItem;
	}
	
	/**
	 * Returns a new {@code RadioMenuItem} instance.
	 * 
	 * @param text the value of the property {@code text}
	 * @param eventHandler the value of the property {@code onAction}
	 * @param toggleGroup a {@code ToggleGroup}
	 * @param isSelected {@code true} if, and only if, the {@code RadioMenuItem} {@code Toggle} should be selected, {@code false} otherwise
	 * @return a new {@code RadioMenuItem} instance
	 */
	public static RadioMenuItem newRadioMenuItem(final String text, final EventHandler<ActionEvent> eventHandler, final ToggleGroup toggleGroup, final boolean isSelected) {
		final
		RadioMenuItem radioMenuItem = new RadioMenuItem();
		radioMenuItem.setOnAction(eventHandler);
		radioMenuItem.setText(text);
		radioMenuItem.setToggleGroup(toggleGroup);
		radioMenuItem.setSelected(isSelected);
		
		return radioMenuItem;
	}
	
	/**
	 * Returns a new {@code Region} instance.
	 * 
	 * @param top the top offset
	 * @param right the right offset
	 * @param bottom the bottom offset
	 * @param left the left offset
	 * @return a new {@code Region} instance
	 */
	public static Region newRegion(final double top, final double right, final double bottom, final double left) {
		final
		Region region = new Region();
		region.setPadding(new Insets(top, right, bottom, left));
		
		return region;
	}
	
	/**
	 * Returns a new {@code Slider} instance.
	 * 
	 * @param min the value of the property {@code min}
	 * @param max the value of the property {@code max}
	 * @param value the value of the property {@code value}
	 * @param blockIncrement the value of the property {@code blockIncrement}
	 * @param majorTickUnit the value of the property {@code majorTickUnit}
	 * @param showTickLabels the value of the property {@code showTickLabels}
	 * @param showTickMarks the value of the property {@code showTickMarks}
	 * @param snapToTicks the value of the property {@code snapToTicks}
	 * @param changeListener a {@code ChangeListener}
	 * @return a new {@code Slider} instance
	 */
	public static Slider newSlider(final double min, final double max, final double value, final double blockIncrement, final double majorTickUnit, final boolean showTickLabels, final boolean showTickMarks, final boolean snapToTicks, ChangeListener<? super Number> changeListener) {
		final
		Slider slider = new Slider();
		slider.setBlockIncrement(blockIncrement);
		slider.setMajorTickUnit(majorTickUnit);
		slider.setMax(max);
		slider.setMin(min);
		slider.setShowTickLabels(showTickLabels);
		slider.setShowTickMarks(showTickMarks);
		slider.setSnapToTicks(snapToTicks);
		slider.setValue(value);
		slider.valueProperty().addListener(changeListener);
		
		return slider;
	}
}