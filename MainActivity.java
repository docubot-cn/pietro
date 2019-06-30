package com.example.pm.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.io.File;
import java.nio.file.Path;

public class MainActivity extends AppCompatActivity {

    /*
     * Creates the README.md markdown file with a H2 section
     * named Project and containing the passed string as project name.
     * returns:  1 = error
     *           0 = ok
     */
    public int createProject(String projectName)
    {
        int ret=1;

        try {
            // catches IOException below
            FileOutputStream fOut = openFileOutput("README.md",
                    MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            String contentText =
                    "# Template\n" +
                            "\n## Project\n\n"+
                            projectName+
                            "\n";
            osw.write(contentText);

            /* ensure that everything is
             * really written out and close */
            osw.flush();
            osw.close();
            ret=0;

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return ret;
    }
    /*
     * Adds a seciont to the README.md markdown file.
     * The section is added preceded and followed by a blank line and
     * using the Header level specified
     * returns:  1 = error
     *           0 = ok
     */
    public int addSectionToProject(String sectionName, int level)
    {
        int ret=1;
        try {

        // catches IOException below
        FileOutputStream fAppend = openFileOutput("README.md",
                MODE_APPEND);
        OutputStreamWriter osw = new OutputStreamWriter(fAppend);

        // Write the string to the file
        String head="\n";
        for(int i=0;i<level;i++)
            head+=("#");
        head+=" ";
        osw.write(head+sectionName+"\n\n");

        /* ensure that everything is
         * really written out and close */
        osw.flush();
        osw.close();
        ret=0;

        } catch (IOException ioe)
        {ioe.printStackTrace();}

        return ret;
    }
    /*
     * Finds a section in the README.md file and points to the line containing
     * the beginning of the next section or the end of the file.
     * receives the section name and level
     * returns: -1 = error
     *           0 = not fund
     *          >0 = line number os the beginning of next same level section,
     *               or first line past the end of the file where to add
     *               the new content
     */
    public int findSectionInProject(String sectionName, int level)
    {
        int ret=-1; //not found
        String levelMarker="";
        Log.d("Test_TAG","findSectionInProject sectionName = "+sectionName);
        try {
            FileInputStream fRead = openFileInput("README.md");
            boolean found = false;
            Scanner scanner = new Scanner(fRead);
            for (int i = 0; i < level; i++)
                levelMarker += "#";
            //now read the file line by line...
            Log.d("Test_TAG","findSectionInProject levelMarker = "+levelMarker);
            int lineNum = 0;
            String line = "";
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                Log.d("Test_TAG","findSectionInProject line = "+line);
                lineNum++;
                if (!found) {
                    if (line.contains(levelMarker + " " + sectionName)) {
                        found = true;
                    }
                } else {
                    if (line.startsWith(levelMarker)) {
                        Log.d("Test_TAG","findSectionInProject found = "+found);
                        Log.d("Test_TAG","findSectionInProject lineNum = "+lineNum);
                        ret=lineNum;
                        break;
                    }
                }
                Log.d("Test_TAG","findSectionInProject found = "+found);
            }
            fRead.close();
            if(!found)
                ret=0;
            else
                ret=lineNum;
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return ret;
    }
    /*
     * receives the section name and level
     * returns: -1 = error
     *           0 = not fund
     *          >0 = line number of the beginning of next same level section,
     *               where to add the new content
     */
    public int AddContentInProject(String textToAdd, String sectionName, int level)
    {

        int ret=-1; //defaults to error
        int positionInFile=findSectionInProject(sectionName, level);
        if(positionInFile<=0)
            return positionInFile;
        try {
            FileInputStream fRead1 = openFileInput("README.md");
            FileOutputStream fAppend1 = openFileOutput("README.bak",MODE_PRIVATE);
            Scanner scanner1 = new Scanner(fRead1);
            OutputStreamWriter osw1 = new OutputStreamWriter(fAppend1);
            String line="";
            while (scanner1.hasNextLine()) {
                line = scanner1.nextLine();
                Log.d("Test_TAG","AddContentInProject simple copy, Copy file: text = "+line);
                osw1.write(line);
            }
            fAppend1.flush();
            fAppend1.close();
            fRead1.close();
            Log.d("Test_TAG","AddContentInProject Copied backup file ");
            FileInputStream fRead = openFileInput("README.bak");
            FileOutputStream fAppend = openFileOutput("README.md",MODE_PRIVATE);
            Log.d("Test_TAG","AddContentInProject opened files");
            Scanner scanner = new Scanner(fRead);
            OutputStreamWriter osw = new OutputStreamWriter(fAppend);
            //now read the file line by line...
            int lineNum = 0;
            while (scanner.hasNextLine()) {
                if (lineNum==positionInFile) {
                    Log.d("Test_TAG","AddContentInProject found, Copy text = "+textToAdd);
                    osw.write(textToAdd);
                    osw.write("\n\n");
                }
                else
                {
                    line = scanner.nextLine();
                    Log.d("Test_TAG","AddContentInProject simple copy, Copy text = "+line);
                    osw.write(line);
                }
                lineNum++;
            }
            fAppend.flush();
            fAppend.close();
            fRead.close();
            ret=lineNum;
        }catch (IOException ioe) {
            Log.d("Test_TAG","AddContentInProject exception!");
            ioe.printStackTrace();
        }
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        int ret;
        ret=createProject("PietroTest");
        if(ret==0)
        {
            ret=addSectionToProject("OVERVIEW",3);
        }
        if(ret==0)
        {
            ret=addSectionToProject("Brainstorm",2);
        }
        if(ret==0)
        {
            ret=addSectionToProject("Useful links",2);
        }
        if(ret==0)
        {
            ret=addSectionToProject("The Team",2);
        }
        if(ret==0)
        {
            ret=addSectionToProject("Project Inspiration",2);
        }
        if(ret==0)
        {
            ret=addSectionToProject("BOM",2);
        }
        if(ret==0)
        {
            ret=addSectionToProject("Materials/Parts list",3);
        }
        if(ret==0)
        {
            ret=addSectionToProject("3D Design",2);
        }
        if(ret==0)
        {
            ret=addSectionToProject("Electronics and Programming",2);
        }
        if(ret==0)
        {
            ret=addSectionToProject("Assembly instructions",2);
        }
        if(ret==0)
        {
            ret=addSectionToProject("License",2);
        }
        if(ret==0)
        {
            ret=addSectionToProject("Call to action",2);
        }
        ret=AddContentInProject("added text in section BOM","BOM",2);
        Log.d("Test_TAG","Ret = "+ret);
        ret=AddContentInProject("added text in section Call to action","Call to action",2);
        Log.d("Test_TAG","Ret = "+ret);
        ret=AddContentInProject("added text in section NOT FOUND","NOT FOUND",2);
        Log.d("Test_TAG","Ret = "+ret);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
