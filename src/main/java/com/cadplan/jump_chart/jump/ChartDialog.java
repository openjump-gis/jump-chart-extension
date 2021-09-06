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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.cadplan.jump_chart.designer.GridBagDesigner;
import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.OKCancelApplyPanel;

/**
 * User: geoff Date: 29/04/2007 Time: 08:26:10 Copyright 2007 Geoffrey G Roy.
 */
public class ChartDialog extends JDialog implements ActionListener,
        ItemListener {

    private static final long serialVersionUID = 1L;
    private static final I18N i18n = JumpChartPlugIn.I18N;
    boolean debug = false;
    JLabel nameLabel, attributeLabel, typeLabel, sizeLabel, scaleLabel,
            widthLabel, originLabel, underLabel;
    JTextField nameField, scaleField, widthField, xorigField, yorigField;
    JComboBox typeCombo, underLabelCombo;
    JCheckBox legendCB, orderCB, clockwiseCB, showScaleCB, showLabelsCB,
            showLocalScaleCB, linearCB, underLabelCB;
    JRadioButton uniformRB, variableRB, autoRB, fixedRB;
    JButton cancelButton, aboutButton, acceptButton, setAllButton,
            clearAllButton, orderButton;
    String[] typeNames = ChartParams.typeNames;
    JPanel attributePanel;
    JCheckBox[] selectAttribute;
    Vector<ChartAttribute> orderedAttributes;
    ButtonGroup sizeGroup, scaleGroup;
    Vector<ChartAttribute> attributes;
    GridBagDesigner gb;
    int currentType = ChartParams.chartType;
    String[] underLabelOptions;
    JPanel mainPanel = new JPanel();
    OKCancelApplyPanel okPanel = new OKCancelApplyPanel();

    public ChartDialog(String layerName, Vector<ChartAttribute> attributes,
            I18N i18n) {
        super(new JFrame(), "Chart Dialog", true);

        setIconImage(GUIUtil.resize(JumpChartPlugIn.ICON, 12).getImage());

        this.attributes = attributes;
        ChartParams.reloadDialog = false;

        // System.out.println("Creating ChartDialog" + "  Attributes size=" +
        // attributes.size());

        ChartParams.attributes = new ChartAttribute[attributes.size()];
        final boolean[] oldInclude = ChartParams.includeAttribute;
        ChartParams.includeAttribute = new boolean[attributes.size()];
        for (int i = 0; i < attributes.size(); i++) {
            ChartParams.includeAttribute[i] = false;
        }
        final int[] oldAttributeOrder = ChartParams.attributeOrder;
        ChartParams.attributeOrder = new int[attributes.size()];
        underLabelOptions = new String[attributes.size()];
        for (int i = 0; i < attributes.size(); i++) {

            ChartParams.attributes[i] = attributes.elementAt(i);
            ChartParams.attributeOrder[i] = -1;
            underLabelOptions[i] = ChartParams.attributes[i].name;
            // System.out.println("layerName="+layerName+"  from Params: :"+ChartParams.layerName);
            if (ChartParams.layerName != null
                    && layerName.equals(ChartParams.layerName)) {
                try {
                    ChartParams.includeAttribute[i] = oldInclude[i];
                    ChartParams.attributeOrder[i] = oldAttributeOrder[i];
                } catch (final Exception ex) {

                }
            } else {
                ChartParams.includeAttribute[i] = false;
            }
        }
        ChartParams.layerName = layerName;
        // System.out.println("Setting layer name: "+layerName+"  No of attr.="+ChartParams.attributes.length);
        // [Giuseppe Aruta 2018-04-04] workaround to close in a proper way the
        // dialog
        // <Bug: when clicking on "x" button the chart was create anyhow>
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                ChartParams.cancelled = true;
                ChartParams.chartType = typeCombo.getSelectedIndex();
                dispose();
            }
        });
        init();
    }

    public void init() {
        mainPanel = new JPanel(new GridBagLayout());
        gb = new GridBagDesigner(mainPanel);
        nameLabel = new JLabel(i18n.get("JumpChart.Dialog.SelectedLayer"));
        gb.setPosition(0, 0);
        gb.setInsets(10, 10, 0, 5);
        gb.addComponent(nameLabel);

        nameField = new JTextField(ChartParams.layerName);
        nameField.setEditable(false);
        gb.setPosition(1, 0);
        gb.setInsets(10, 0, 0, 10);
        gb.setSpan(3, 1);
        gb.setFill(GridBagConstraints.HORIZONTAL);
        gb.addComponent(nameField);

        attributeLabel = new JLabel(i18n.get("JumpChart.Dialog.Attributes"));
        gb.setPosition(0, 1);
        gb.setInsets(5, 10, 0, 5);
        gb.addComponent(attributeLabel);

        setAllButton = new JButton(i18n.get("JumpChart.Dialog.SelectAll"));
        gb.setPosition(0, 2);
        gb.setInsets(5, 10, 0, 5);
        gb.setFill(GridBagConstraints.HORIZONTAL);
        gb.addComponent(setAllButton);
        setAllButton.addActionListener(this);

        clearAllButton = new JButton(i18n.get("JumpChart.Dialog.ClearAll"));
        gb.setPosition(0, 3);
        gb.setInsets(5, 10, 0, 5);
        gb.setFill(GridBagConstraints.HORIZONTAL);
        gb.addComponent(clearAllButton);
        clearAllButton.addActionListener(this);

        orderButton = new JButton(i18n.get("JumpChart.Dialog.Order"));
        gb.setPosition(0, 4);
        gb.setInsets(5, 10, 0, 5);
        gb.setFill(GridBagConstraints.HORIZONTAL);
        gb.addComponent(orderButton);
        orderButton.addActionListener(this);

        createSelectPanel();
        addAttributePanel();

        typeLabel = new JLabel(i18n.get("JumpChart.Dialog.ChartType"));
        gb.setPosition(0, 6);
        gb.setInsets(5, 10, 0, 5);
        gb.addComponent(typeLabel);

        typeCombo = new JComboBox(typeNames);
        gb.setPosition(1, 6);
        gb.setInsets(5, 0, 0, 10);
        gb.addComponent(typeCombo);
        typeCombo.setSelectedIndex(ChartParams.chartType);
        typeCombo.addActionListener(this);

        clockwiseCB = new JCheckBox(i18n.get("JumpChart.Dialog.Clockwise"));
        gb.setPosition(2, 6);
        gb.setInsets(5, 0, 0, 5);
        gb.addComponent(clockwiseCB);
        clockwiseCB.setSelected(ChartParams.clockwise);

        legendCB = new JCheckBox(i18n.get("JumpChart.Dialog.Legend"));
        gb.setPosition(3, 6);
        gb.setInsets(5, 0, 0, 5);
        gb.setAnchor(GridBagConstraints.WEST);
        gb.addComponent(legendCB);
        legendCB.setSelected(ChartParams.includeLegend);

        // orderCB = new JCheckBox("Order");
        // gb.setPosition(3,6);
        // gb.setInsets(5,0,0,10);
        // gb.addComponent(orderCB);
        // orderCB.setSelected(ChartParams.ordered);

        sizeLabel = new JLabel(i18n.get("JumpChart.Dialog.Size"));
        gb.setPosition(0, 7);
        gb.setInsets(5, 10, 0, 5);
        gb.addComponent(sizeLabel);

        uniformRB = new JRadioButton(i18n.get("JumpChart.Dialog.Uniform"));
        gb.setPosition(1, 7);
        gb.setInsets(5, 0, 0, 0);
        gb.setAnchor(GridBagConstraints.WEST);
        gb.addComponent(uniformRB);

        variableRB = new JRadioButton(i18n.get("JumpChart.Dialog.Variable"));
        gb.setPosition(2, 7);
        gb.setInsets(5, 0, 0, 0);
        gb.setAnchor(GridBagConstraints.WEST);
        gb.addComponent(variableRB);

        linearCB = new JCheckBox(i18n.get("JumpChart.Dialog.Linear"));
        gb.setPosition(3, 7);
        gb.setInsets(5, 0, 0, 10);
        gb.setAnchor(GridBagConstraints.WEST);
        gb.addComponent(linearCB);
        linearCB.setSelected(ChartParams.linearScale);

        sizeGroup = new ButtonGroup();
        sizeGroup.add(uniformRB);
        sizeGroup.add(variableRB);
        uniformRB.setSelected(ChartParams.uniformSize);
        variableRB.setSelected(!ChartParams.uniformSize);

        scaleLabel = new JLabel(i18n.get("JumpChart.Dialog.Scale"));
        gb.setPosition(0, 8);
        gb.setInsets(5, 10, 0, 5);
        gb.addComponent(scaleLabel);

        autoRB = new JRadioButton(i18n.get("JumpChart.Dialog.Auto"));
        gb.setPosition(1, 8);
        gb.setInsets(5, 0, 0, 0);
        gb.setAnchor(GridBagConstraints.WEST);
        gb.addComponent(autoRB);

        fixedRB = new JRadioButton(i18n.get("JumpChart.Dialog.Fixed"));
        gb.setPosition(2, 8);
        gb.setInsets(5, 0, 0, 0);
        gb.setAnchor(GridBagConstraints.WEST);
        gb.addComponent(fixedRB);

        scaleGroup = new ButtonGroup();
        scaleGroup.add(fixedRB);
        scaleGroup.add(autoRB);
        autoRB.setSelected(ChartParams.autoScale);
        fixedRB.setSelected(!ChartParams.autoScale);

        scaleField = new JTextField(8);
        scaleField.setText(String.valueOf(ChartParams.scaleValue));
        gb.setPosition(3, 8);
        gb.setInsets(5, 10, 0, 10);
        gb.addComponent(scaleField);

        showScaleCB = new JCheckBox(i18n.get("JumpChart.Dialog.ShowScale"));
        gb.setPosition(1, 9);
        gb.setInsets(5, 0, 0, 0);
        gb.setAnchor(GridBagConstraints.WEST);
        gb.addComponent(showScaleCB);
        showScaleCB.setSelected(ChartParams.showScale);

        showLocalScaleCB = new JCheckBox(
            i18n.get("JumpChart.Dialog.LocalScale"));
        gb.setPosition(2, 9);
        gb.setInsets(5, 0, 0, 0);
        gb.setAnchor(GridBagConstraints.WEST);
        gb.addComponent(showLocalScaleCB);
        showLocalScaleCB.setSelected(ChartParams.localScale);

        showLabelsCB = new JCheckBox(i18n.get("JumpChart.Dialog.ShowLabels"));
        gb.setPosition(1, 10);
        gb.setInsets(5, 0, 0, 10);
        gb.addComponent(showLabelsCB);
        showLabelsCB.setSelected(ChartParams.showLabels);

        widthLabel = new JLabel(i18n.get("JumpChart.Dialog.Width"));
        gb.setPosition(2, 10);
        gb.setInsets(5, 10, 0, 5);
        gb.setAnchor(GridBagConstraints.EAST);
        gb.addComponent(widthLabel);

        widthField = new JTextField(8);
        widthField.setText(String.valueOf(ChartParams.barWidth));
        gb.setPosition(3, 10);
        gb.setInsets(5, 10, 0, 10);
        gb.addComponent(widthField);

        originLabel = new JLabel(i18n.get("JumpChart.Dialog.Offsets"));
        gb.setPosition(0, 11);
        gb.setInsets(5, 10, 0, 5);
        gb.setSpan(3, 1);
        gb.setAnchor(GridBagConstraints.WEST);
        gb.addComponent(originLabel);

        final JLabel xLabel = new JLabel("X");
        gb.setPosition(0, 12);
        gb.setInsets(5, 10, 0, 5);
        gb.setAnchor(GridBagConstraints.EAST);
        gb.addComponent(xLabel);

        xorigField = new JTextField(8);
        gb.setPosition(1, 12);
        gb.addComponent(xorigField);
        xorigField.setText(String.valueOf(ChartParams.originX));

        final JLabel yLabel = new JLabel("Y");
        gb.setPosition(2, 12);
        gb.setInsets(5, 10, 0, 5);
        gb.setAnchor(GridBagConstraints.EAST);
        gb.addComponent(yLabel);

        yorigField = new JTextField(8);
        gb.setPosition(3, 12);
        gb.addComponent(yorigField);
        yorigField.setText(String.valueOf(ChartParams.originY));

        underLabel = new JLabel(i18n.get("JumpChart.Dialog.UnderLabels"));
        gb.setPosition(0, 13);
        gb.setInsets(5, 10, 0, 5);
        // gb.setAnchor(GridBagConstraints.EAST);
        gb.addComponent(underLabel);

        underLabelCB = new JCheckBox("");
        gb.setPosition(1, 13);
        gb.setInsets(5, 10, 0, 5);
        gb.setAnchor(GridBagConstraints.WEST);
        gb.addComponent(underLabelCB);
        underLabelCB.setSelected(ChartParams.showUnderLabels);

        // underLabelCombo= new JComboBox(underLabelOptions);
        // gb.setPosition(2,13);
        // gb.setInsets(5, 10, 0, 5);
        // gb.setFill(GridBagConstraints.HORIZONTAL);
        // gb.setSpan(2,1);
        // gb.addComponent(underLabelCombo);
        // underLabelCombo.setSelectedIndex(ChartParams.underLabelIndex);

        // cancelButton = new JButton(iPlug.get("JumpChart.Dialog.Cancel"));
        // gb.setPosition(3, 14);
        // gb.setInsets(20, 10, 10, 0);
        // gb.addComponent(cancelButton);
        // cancelButton.addActionListener(this);

        aboutButton = new JButton(i18n.get("JumpChart.Dialog.About"));
        gb.setPosition(3, 13);
        gb.setInsets(20, 10, 10, 0);
        gb.addComponent(aboutButton);
        aboutButton.addActionListener(this);

        // acceptButton = new JButton(iPlug.get("JumpChart.Dialog.Accept"));
        // gb.setPosition(2, 14);
        // gb.setInsets(20, 10, 10, 10);
        // gb.addComponent(acceptButton);
        // acceptButton.addActionListener(this);

        okPanel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (okPanel.wasOKPressed()) {
                    okAction(e);
                } else {
                    closeAction(e);
                }
            }
        });
        add(mainPanel, BorderLayout.NORTH);
        add(okPanel, BorderLayout.SOUTH);
        pack();
        setLocation(100, 100);
        setVisible(true);

    }

    private void createSelectPanel() {
        attributePanel = new JPanel();
        final GridBagDesigner gba = new GridBagDesigner(attributePanel);
        selectAttribute = new JCheckBox[ChartParams.attributes.length];
        for (int i = 0; i < ChartParams.attributes.length; i++) {
            if (debug) {
                System.out.println("Adding Attr: " + ChartParams.attributes[i]);
            }
            selectAttribute[i] = new JCheckBox(ChartParams.attributes[i].name);
            gba.setPosition(0, i);
            gba.setInsets(0, 10, 0, 10);
            gba.setFill(GridBagConstraints.HORIZONTAL);
            gba.setAnchor(GridBagConstraints.WEST);
            gba.setWeight(1.0, 0.0);
            gba.addComponent(selectAttribute[i]);
            selectAttribute[i].setSelected(ChartParams.includeAttribute[i]);
            selectAttribute[i].addItemListener(this);

        }
    }

    private void addAttributePanel() {
        final JScrollPane scrollPane = new JScrollPane(attributePanel);
        scrollPane.setPreferredSize(new Dimension(0, 120));
        gb.setPosition(1, 1);
        gb.setInsets(5, 0, 0, 10);
        gb.setSpan(3, 5);
        gb.setWeight(1.0, 1.0);
        gb.setFill(GridBagConstraints.BOTH);
        gb.addComponent(scrollPane);
        final JScrollBar vsb = scrollPane.getVerticalScrollBar();
        vsb.setUnitIncrement(20);
    }

    public Vector<ChartAttribute> getSelections() {
        return attributes;
    }

    private void clearOrder() {
        Arrays.fill(ChartParams.attributeOrder, -1);
    }

    @Override
    public void itemStateChanged(ItemEvent ev) {
        for (int i = 0; i < ChartParams.attributes.length; i++) {
            if (ev.getSource() == selectAttribute[i]) {
                ChartParams.includeAttribute[i] = selectAttribute[i]
                        .isSelected();
            }
            // System.out.println("i="+i+"  selected:"+ChartParams.includeAttribute[i]);
        }
        clearOrder();
    }

    public void closeAction(ActionEvent ev) {
        ChartParams.cancelled = true;
        ChartParams.chartType = typeCombo.getSelectedIndex();
        dispose();
    }

    public void okAction(ActionEvent ev) {
        attributes = new Vector<>(10, 2);
        int numSelected = 0;
        for (int i = 0; i < selectAttribute.length; i++) {
            if (ChartParams.includeAttribute[i]) {
                numSelected++;
            }
        }
        final ChartAttribute[] tempAttributes = new ChartAttribute[numSelected];
        int k = 0;
        for (int i = 0; i < selectAttribute.length; i++) {
            ChartParams.includeAttribute[i] = selectAttribute[i].isSelected();
            // if(ChartParams.includeAttribute[i])
            // attributes.addElement(ChartParams.attributes[i]);
            final int order = ChartParams.attributeOrder[i];
            if (ChartParams.includeAttribute[i]) {
                if (order >= 0) {
                    tempAttributes[order] = ChartParams.attributes[i];
                } else {
                    tempAttributes[k++] = ChartParams.attributes[i];
                }
            }
        }
        for (int i = 0; i < numSelected; i++) {
            attributes.addElement(tempAttributes[i]);
        }
        if (attributes.size() <= 0) {
            JOptionPane.showMessageDialog(this,
                i18n.get("JumpChart.message3"), "Error...",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        ChartParams.autoScale = autoRB.isSelected();
        ChartParams.uniformSize = uniformRB.isSelected();
        try {
            ChartParams.scaleValue = Double.parseDouble(scaleField.getText());
            ChartParams.barWidth = Double.parseDouble(widthField.getText());
        } catch (final NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                i18n.get("JumpChart.message2"), "Error...",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        ChartParams.includeLegend = legendCB.isSelected();
        ChartParams.clockwise = clockwiseCB.isSelected();
        ChartParams.chartType = typeCombo.getSelectedIndex();
        ChartParams.showScale = showScaleCB.isSelected();
        ChartParams.cancelled = false;
        ChartParams.showLabels = showLabelsCB.isSelected();
        ChartParams.localScale = showLocalScaleCB.isSelected();
        ChartParams.linearScale = linearCB.isSelected();

        try {
            ChartParams.originX = Integer.parseInt(xorigField.getText());
            ChartParams.originY = Integer.parseInt(yorigField.getText());
        } catch (final NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                i18n.get("JumpChart.message4"), "Error...",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        ChartParams.showUnderLabels = underLabelCB.isSelected();
        // ChartParams.underLabelIndex = underLabelCombo.getSelectedIndex();
        // ChartParams.underLabelName = (String)
        // underLabelCombo.getSelectedItem();

        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent ev) {

        if (ev.getSource() == typeCombo) {

            if (typeCombo.getSelectedIndex() == ChartParams.LABELS) {
                if (currentType != ChartParams.LABELS) {
                    ChartParams.chartType = ChartParams.LABELS;
                    ChartParams.cancelled = true;
                    ChartParams.reloadDialog = true;
                    dispose();
                }
            } else {
                if (currentType == ChartParams.LABELS) {
                    ChartParams.chartType = typeCombo.getSelectedIndex();
                    ChartParams.cancelled = true;
                    ChartParams.reloadDialog = true;
                    dispose();
                }
            }
            // System.out.println("Type Item selected");
        }
        if (ev.getSource() == cancelButton) {
            ChartParams.cancelled = true;
            ChartParams.chartType = typeCombo.getSelectedIndex();
            dispose();
        }

        if (ev.getSource() == acceptButton) {
            attributes = new Vector<>(10, 2);
            int numSelected = 0;
            for (int i = 0; i < selectAttribute.length; i++) {
                if (ChartParams.includeAttribute[i]) {
                    numSelected++;
                }
            }
            final ChartAttribute[] tempAttributes = new ChartAttribute[numSelected];
            int k = 0;
            for (int i = 0; i < selectAttribute.length; i++) {
                ChartParams.includeAttribute[i] = selectAttribute[i]
                        .isSelected();
                // if(ChartParams.includeAttribute[i])
                // attributes.addElement(ChartParams.attributes[i]);
                final int order = ChartParams.attributeOrder[i];
                if (ChartParams.includeAttribute[i]) {
                    if (order >= 0) {
                        tempAttributes[order] = ChartParams.attributes[i];
                    } else {
                        tempAttributes[k++] = ChartParams.attributes[i];
                    }
                }
            }
            for (int i = 0; i < numSelected; i++) {
                attributes.addElement(tempAttributes[i]);
            }
            if (attributes.size() <= 0) {
                JOptionPane.showMessageDialog(this,
                    i18n.get("JumpChart.message3"), "Error...",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            ChartParams.autoScale = autoRB.isSelected();
            ChartParams.uniformSize = uniformRB.isSelected();
            try {
                ChartParams.scaleValue = Double.parseDouble(scaleField
                        .getText());
                ChartParams.barWidth = Double.parseDouble(widthField.getText());
            } catch (final NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    i18n.get("JumpChart.message2"), "Error...",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            ChartParams.includeLegend = legendCB.isSelected();
            ChartParams.clockwise = clockwiseCB.isSelected();
            ChartParams.chartType = typeCombo.getSelectedIndex();
            ChartParams.showScale = showScaleCB.isSelected();
            ChartParams.cancelled = false;
            ChartParams.showLabels = showLabelsCB.isSelected();
            ChartParams.localScale = showLocalScaleCB.isSelected();
            ChartParams.linearScale = linearCB.isSelected();

            try {
                ChartParams.originX = Integer.parseInt(xorigField.getText());
                ChartParams.originY = Integer.parseInt(yorigField.getText());
            } catch (final NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    i18n.get("JumpChart.message4"), "Error...",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            ChartParams.showUnderLabels = underLabelCB.isSelected();
            // ChartParams.underLabelIndex = underLabelCombo.getSelectedIndex();
            // ChartParams.underLabelName = (String)
            // underLabelCombo.getSelectedItem();

            dispose();
        }

        if (ev.getSource() == aboutButton) {
            JOptionPane
                    .showMessageDialog(
                            null,
                            "JumpChart Vers:"
                                    + ChartParams.version
                                    + "\nBased on an idea from Beate Stollberg by Geoffrey G. Roy\nCopyright 2007-2010, Cadplan",
                            "About...", JOptionPane.INFORMATION_MESSAGE);
        }

        if (ev.getSource() == setAllButton) {
            for (int i = 0; i < ChartParams.attributes.length; i++) {
                ChartParams.includeAttribute[i] = true;
                selectAttribute[i].setSelected(true);
            }
        }

        if (ev.getSource() == clearAllButton) {
            for (int i = 0; i < ChartParams.attributes.length; i++) {
                ChartParams.includeAttribute[i] = false;
                selectAttribute[i].setSelected(false);
            }
        }

        if (ev.getSource() == orderButton) {
            // System.out.println("Ordering");
            final AttributeOrder attributeOder = new AttributeOrder(this);
        }

    }
}
