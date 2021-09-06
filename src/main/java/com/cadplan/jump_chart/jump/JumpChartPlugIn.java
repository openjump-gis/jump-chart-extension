/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2006 Cadplan
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package com.cadplan.jump_chart.jump;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.MenuNames;

import java.util.Objects;

/**
 * User: geoff Date: 28/04/2007 Time: 09:40:22 Copyright 2007 Geoffrey G Roy.
 */
public class JumpChartPlugIn extends AbstractPlugIn {

    public static final I18N I18N = com.vividsolutions.jump.I18N.getInstance("com.cadplan.jump_chart");
    public static final ImageIcon ICON = new ImageIcon(Objects.requireNonNull(JumpChartPlugIn.class.getResource("charticon.gif")));

    @Override
    public void initialize(PlugInContext context) {
        ChartParams.setNames(I18N);
        final MultiEnableCheck mcheck = new MultiEnableCheck();
        final String menuName = MenuNames.PLUGINS;
        final String menuItem = I18N.get("JumpChart.MenuItem");
        context.getFeatureInstaller().addMainMenuPlugin(this,
                new String[] { menuName }, menuItem, false, getIcon(), mcheck);
        context.getWorkbenchFrame()
                .getToolBar()
                .addPlugIn(ICON, this, mcheck, context.getWorkbenchContext());

    }

    public Icon getIcon() {
        return ICON;
    }

    @Override
    public String getName() {
        return I18N.get("JumpChart.MenuItem");
    }

    @Override
    public boolean execute(PlugInContext context) {
        final Chart chart = new Chart(context, I18N);
        return true;
    }

}
