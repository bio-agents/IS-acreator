package org.isaagents.isacreator.common.button;

import org.isaagents.isacreator.common.UIHelper;

import java.awt.*;


public enum ButtonType {


    BLUE(UIHelper.PETER_RIVER, UIHelper.BELIZE_HOLE),
    GREEN(UIHelper.LIGHT_GREEN_COLOR, UIHelper.DARK_GREEN_COLOR),
    EMERALD(UIHelper.EMERALD, UIHelper.NEPHRITIS),
    RED(UIHelper.POMEGRANATE, UIHelper.ALIZARIN),
    GREY(new Color(236,240,241), new Color(230,230,230)),
    ORANGE(UIHelper.CARROT, UIHelper.PUMPKIN);

    private Color defaultColor;
    private Color hoverColor;

    ButtonType(Color defaultColor, Color hoverColor) {
        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public Color getHoverColor() {
        return hoverColor;
    }
}
