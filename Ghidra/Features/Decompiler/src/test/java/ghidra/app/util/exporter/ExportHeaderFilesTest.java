/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.app.util.exporter;

import static org.junit.Assert.*;

import org.junit.*;

import java.io.File;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

import generic.test.AbstractGenericTest;
import ghidra.program.model.listing.Program;
import ghidra.program.model.lang.*;
import ghidra.program.util.DefaultLanguageService;
import ghidra.util.task.TaskMonitor;
import ghidra.program.database.ProgramDB;
import ghidra.program.model.data.*;
import ghidra.framework.Application;

import generic.jar.ResourceFile;

public class ExportHeaderFilesTest extends AbstractGenericTest{
	private Exporter exporter = new CppExporter(null, false, true, false, false, null);
	private int transactionID;
	private Program program;
	private File testFile = new File("testFile.c");

	@Before
	public void setUp() throws Exception {
		Language language = getLanguage("x86:LE:32:default");
		program = new ProgramDB("Test", language, language.getDefaultCompilerSpec(), this);
		transactionID = program.startTransaction("Test");
	}
	
	@After
	public void tearDown() throws Exception {
		if (program != null) {
			program.endTransaction(transactionID, true);
			program.release(this);
		}

		testFile.delete();
	}
	

	@Test
	public void testExportHeaderFilesOfDataTypes() throws Exception{
		ResourceFile gdtFile = Application.findDataFileInAnyModule("typeinfo/clib/clib_gcc_x86_64-linux-gnu.gdt");
		FileDataTypeManager dtm = FileDataTypeManager.openFileArchive(gdtFile, false);
		SourceArchive sourceArchive = dtm.getLocalSourceArchive();
		DataTypeManager dtMgr = program.getDataTypeManager();
		
		DataType dt1 = getDataType("testDT1", "stdio.h");
		dt1.setSourceArchive(sourceArchive);
		DataType dt2 = getDataType("testDT2", "sys/types.h");
		dt2.setSourceArchive(sourceArchive);
		dtMgr.addDataType(dt1, DataTypeConflictHandler.DEFAULT_HANDLER);
		dtMgr.addDataType(dt2, DataTypeConflictHandler.DEFAULT_HANDLER);
		
		assertTrue(exporter.export(testFile, program, null, TaskMonitor.DUMMY));
		List<String> lines = Files.readAllLines(Paths.get("testFile.c"));

		assertEquals("#include <stdio.h>", lines.get(0));
		assertEquals("#include <sys/types.h>", lines.get(1));
	}
	
	private DataType getDataType(String name, String catName)
	{
		EnumDataType dt = new EnumDataType("define_" + name, 8);
		dt.add(name, 1);
		dt.setLength(dt.getMinimumPossibleLength());
		CategoryPath categoryPath = new CategoryPath(CategoryPath.ROOT, catName.split("/"));
		dt.setCategoryPath(categoryPath);
		return dt;
	}
	
	private static Language getLanguage(String languageName) throws LanguageNotFoundException {
		LanguageService languageService = DefaultLanguageService.getLanguageService();
		return languageService.getLanguage(new LanguageID(languageName));
	}
}
