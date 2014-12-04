package com.highlanderchef;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Utility
{
	static public ProgressBar DisplaySpinner(Context iContext, ViewGroup iLayout)
	{
		ProgressBar ISpinner = new ProgressBar(iContext);
		ViewGroup.LayoutParams Params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		iLayout.addView(ISpinner, Params);
		return ISpinner;
	}
	static public void RemoveSpinner(ProgressBar iSpinner)
	{
		if (iSpinner != null)
			((ViewGroup) iSpinner.getParent()).removeView(iSpinner);
	}
	static public void displayErrorToasts(Context context, Integer errorValue, Integer duration) {

		Toast toastErrorMessage;
		switch(errorValue) {
		case -1:
			toastErrorMessage = Toast.makeText(context, "Sorry! We could not load the recipe you want to view. Check your connection!", duration);
			toastErrorMessage.setGravity(Gravity.CENTER, 0, 0); //gravity, x-offset, y-offset
			toastErrorMessage.show();
			break;
		case -2:
			toastErrorMessage = Toast.makeText(context, "Sorry! We could not load the recipe you want to view. Check your connection!", duration);
			toastErrorMessage.setGravity(Gravity.CENTER, 0, 0); //gravity, x-offset, y-offset
			toastErrorMessage.show();
			break;
		default:
			break;

		}
	}
}

class ID_Maker
{
	private static ID_Maker instance = null;
	private int CurrIdNum;
	protected ID_Maker() {
		CurrIdNum = 1;
	}
	public static ID_Maker getInstance() {
		if(instance == null) {
			instance = new ID_Maker();
		}
		return instance;
	}
	public int useCurrID()
	{
		int returnValue = CurrIdNum;
		CurrIdNum = CurrIdNum + 1;
		return returnValue;
	}
}

class LeveledCheckBox extends CheckBox
{
	private final int cLevel;
	private final ArrayList<LeveledCheckBox> cChildren;
	Category cData;
	public LeveledCheckBox(Context iContext, int iLevel, Category iData)
	{
		super(iContext);
		cChildren = new ArrayList<LeveledCheckBox>();
		cLevel = iLevel;
		cData = iData;
	}
	public int getLevel()
	{
		return cLevel;
	}
	public void addChild(LeveledCheckBox iCheckBox)
	{
		cChildren.add(iCheckBox);
	}
	public void removeChildren()
	{
		cChildren.clear();
	}
	public ArrayList<LeveledCheckBox> getCheck()
	{
		return null;
	}
	public int getDeepestLevel()
	{
		if (cChildren.isEmpty())
			return cLevel;
		else
		{
			int biggest = 0;
			for (int i = 0; i < cChildren.size() ; i++)
				if (cChildren.get(i).getDeepestLevel() > biggest)
					biggest = cChildren.get(i).getDeepestLevel();
			return biggest;
		}
	}
	@Override
	public void bringToFront()
	{
		super.bringToFront();
		for (int i = 0; i < cChildren.size(); i++)
		{
			cChildren.get(i).bringToFront();
		}
	}
}