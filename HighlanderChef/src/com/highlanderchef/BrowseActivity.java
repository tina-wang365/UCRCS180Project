package com.highlanderchef;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
//import android.view.ViewGroup;

public class BrowseActivity extends Activity {

	//variables
	public static final int LEFTPADDING = 14;
	private ArrayList<LeveledCheckBox> CheckBoxList;
	private Button cButton;

	//functions
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CheckBoxList = new ArrayList<LeveledCheckBox>();
		ScrollView iScrollView = new ScrollView(this);
		LinearLayout iLinearLayout = new LinearLayout(this);
		iLinearLayout.setOrientation(LinearLayout.VERTICAL);
		iScrollView.addView(iLinearLayout);
		ID_Maker currID = ID_Maker.getInstance();

		//add search button
		cButton = new Button(this);
		cButton.setText(getResources().getString(R.string.Search_Categories));
		cButton.setId(currID.useCurrID());
		iLinearLayout.addView(cButton);
		cButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View iView) {
				//send category search to server
			}
		});

		//Get categories' names
		ArrayList<String> iCategoryNames = new ArrayList<String>();

		//testing cases
		iCategoryNames.add("foo");
		iCategoryNames.add("bar");
		iCategoryNames.add("baz");

		int currentLevel = 0;
		for (int i = 0; i < iCategoryNames.size() ; i++)
		{
			LeveledCheckBox iCheckBox = new LeveledCheckBox(this,currentLevel);
			iCheckBox.setText(iCategoryNames.get(i).toCharArray(),
					0, iCategoryNames.get(i).length());
			iCheckBox.setId(currID.useCurrID());
			iCheckBox.setOnClickListener(CheckboxGetsChecked(iCheckBox));
			iLinearLayout.addView(iCheckBox);
			CheckBoxList.add(iCheckBox);
		}
		this.setContentView(iScrollView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.browse, menu);

		return true;
	}

	View.OnClickListener CheckboxGetsChecked(final LeveledCheckBox iCheckBox)
	{
		return new View.OnClickListener() {
			@Override
			public void onClick(View iView) {
				if (((CheckBox) iView).isChecked())
				{
					LinearLayout iLinearLayout = ((LinearLayout) iCheckBox.getParent());
					//get categories' names
					ArrayList<String> LowerCategoryList = new ArrayList<String>();

					//testing cases
					LowerCategoryList.add("Childfoo");
					LowerCategoryList.add("Childbar");
					LowerCategoryList.add("Childbaz");

					int currentLevel = ((LeveledCheckBox) iView).getLevel() + 1;
					ID_Maker currID = ID_Maker.getInstance();
					for (int i = 0 ; i < LowerCategoryList.size() ; i++)
					{
						LeveledCheckBox newCheckBox = new LeveledCheckBox(iCheckBox.getContext(),currentLevel);
						newCheckBox.setText(LowerCategoryList.get(i).toCharArray(),
								0, LowerCategoryList.get(i).length());
						newCheckBox.setPadding(LEFTPADDING*currentLevel,0,0,0);
						newCheckBox.setId(currID.useCurrID());
						newCheckBox.setOnClickListener(CheckboxGetsChecked(newCheckBox));
						iCheckBox.addChild(newCheckBox);
						iLinearLayout.addView(newCheckBox,iLinearLayout.indexOfChild(iCheckBox) + 1);
					}
					//Reorder the stack so the children are displayed underneath the parent
					reorderItems();
				}
				else
				{
					//stuff
					;//iCheckBox.removeChildren();
				}
			}
		};
	}

	private void reorderItems()
	{
		cButton.bringToFront();
		for (int i = 0; i < CheckBoxList.size() ; i++)
		{
			CheckBoxList.get(i).bringToFront();
		}
	}
}

class ID_Maker
{
	private static ID_Maker instance = null;
	private int CurrIdNum;
	protected ID_Maker() {
		CurrIdNum = 0;
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
	private final ArrayList<CheckBox> cChildren;
	public LeveledCheckBox(Context iContext, int iLevel)
	{
		super(iContext);
		cChildren = new ArrayList<CheckBox>();
		cLevel = iLevel;
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

