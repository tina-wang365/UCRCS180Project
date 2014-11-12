package com.highlanderchef;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
	private Comm iComm;
	private ArrayList<Category> CategoriesData;

	//functions
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iComm = new Comm();
		CheckBoxList = new ArrayList<LeveledCheckBox>();
		CategoriesData = new ArrayList<Category>();
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
				//get search results and pass them to result page
				Intent intent = new Intent(BrowseActivity.this, BrowseResults.class);
				Bundle bundle = new Bundle();

				ArrayList<ArrayList<String> > SelectedCategories = new ArrayList<ArrayList<String> >();
				int deepestLevel = 0;
				for (int i = 0; i < CheckBoxList.size(); i++)
				{
					if (CheckBoxList.get(i).getLevel() != 0)
						break;
					if (CheckBoxList.get(i).getDeepestLevel() > deepestLevel)
						deepestLevel = CheckBoxList.get(i).getDeepestLevel();
				}
				for (int i = 0; i <= deepestLevel; i++)
					SelectedCategories.add(new ArrayList<String>());
				for (int i = 0; i < CheckBoxList.size(); i++)
				{
					SelectedCategories.get(CheckBoxList.get(i).getLevel()).add(CheckBoxList.get(i).getText().toString());
				}
				for (int i = 0; i < deepestLevel; i++)
					bundle.putStringArrayList(Integer.toString(i), SelectedCategories.get(i));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		//Get categories' names
		ArrayList<String> iCategoryNames = new ArrayList<String>();
		CategoriesData = iComm.getCategories();
		for (int i = 0; i < CategoriesData.size(); i++)
			if (CategoriesData.get(i).level == 0)
				iCategoryNames.add(CategoriesData.get(i).name);

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
					ArrayList<String> LowerCategoryList = getCategoryChildren(iCheckBox.getText().toString(),iCheckBox.getLevel());

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
						CheckBoxList.add(newCheckBox);
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
			if (CheckBoxList.get(i).getLevel() == 0)
				CheckBoxList.get(i).bringToFront();
			else
				break;
		}
	}

	private ArrayList<String> getCategoryChildren(String iName, int iLevel)
	{
		ArrayList<String> Children = new ArrayList<String>();
		boolean foundParent = false;
		int searchLevel = iLevel + 1;
		for (int i = 0; i < CategoriesData.size() ; i++)
		{
			if (CategoriesData.get(i).name == iName)
				foundParent = true;
			if (foundParent && CategoriesData.get(i).level == searchLevel)
				Children.add(CategoriesData.get(i).name);
			if (foundParent && CategoriesData.get(i).level == iLevel)
				return Children;
		}
		return Children;
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
	private final ArrayList<LeveledCheckBox> cChildren;
	public LeveledCheckBox(Context iContext, int iLevel)
	{
		super(iContext);
		cChildren = new ArrayList<LeveledCheckBox>();
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

