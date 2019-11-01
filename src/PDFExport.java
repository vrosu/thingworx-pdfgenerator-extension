import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.slf4j.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.RequestHeaders;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.entities.utils.ThingUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.resources.Resource;
import com.thingworx.things.repository.FileRepositoryThing;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.StringPrimitive;

/**
 * 
 */

/**
 * @author vrosu Jan 28, 2016 2016
 */

public class PDFExport extends Resource {

	private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(PDFExport.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -1395344025018016841L;
	private static final String  COLUMN_TIME_ZONE_NAME = "TimezoneName";
	private static final String  DATA_SHAPE_AVAILABLE_TIME_ZONE = "PDFExport_GetAvailableTimezones";

	/**
	 * 
	 */
	public PDFExport() {
	}

	@ThingworxServiceDefinition(name = "CreatePDF", description = "")
	@ThingworxServiceResult(name = "Result", description = "Contains error message in case of error.", baseType = "STRING")
	public String CreatePDF(
			@ThingworxServiceParameter(name = "Rotated90Deg", description = "Do we apply a 90 degrees rotation or not. Useful for printing on Portrait or Landscape", baseType = "BOOLEAN", aspects = {	"defaultValue:false" }) 
			Boolean rotate90Deg,
			
			@ThingworxServiceParameter(name = "Resolution", description = "Resolution must be in the format x*y, eg. 1366*768", baseType = "STRING", aspects = {"defaultValue:1366*768" }) 
			String resolution,
			
			@ThingworxServiceParameter(name = "AppKey", description = "AppKey", baseType = "STRING") 
			String twAppKey,
			
			@ThingworxServiceParameter(name = "OutputFileName", description = "", baseType = "STRING",aspects = {"defaultValue:Report.pdf" }) 
			String fileName,
			
			@ThingworxServiceParameter(name = "ServerAddress", description = "The address must be ending in /Runtime/index.html#mashup=mashup_name. It will not work with Thingworx/Mashups/mashup_name", baseType = "STRING", aspects = {"defaultValue:https://localhost:8443/Thingworx/Runtime/index.html#mashup=LogViewer" }) 
			String server,
			
			@ThingworxServiceParameter(name = "FileRepository", description = "Choose a file repository where the output file will be stored.", baseType = "THINGNAME", aspects = {"defaultValue:SystemRepository", "thingTemplate:FileRepository" }) 
			String fileRepository,
			
			@ThingworxServiceParameter(name = "DebugUsefontconfig", description = "Enable use of fontconfig when exporting the PDF. Used in case of Redhat OS.", baseType = "BOOLEAN", aspects = {"defaultValue:true" }) 
			Boolean usefontconfig,
			
			@ThingworxServiceParameter(name = "ScreenshotDelaySecond", description = "Add a delay before taking the screenshot in Second", baseType = "INTEGER", aspects = {"defaultValue:0" }) 
			Integer screenshotDelaySecond,
			
			@ThingworxServiceParameter(name = "TimeZoneName", description = "Set a time zone to the broswer emulator", baseType = "STRING") 
			String timeZoneName


	) throws Exception {
		// Variable Declaration
		String result = "";
		String[] resolutions = null;
		int screenshotDelayMS = 0;
		int ajaxResourceTimeout = 2000;
		Timezone timezone = null;
		
		// Validate our parameters are good
		if (rotate90Deg == null)
			rotate90Deg = false;
		
		if(screenshotDelaySecond == null ||  (screenshotDelaySecond != null && screenshotDelaySecond < 0)) {
			screenshotDelaySecond = 0;
		}
		
		if(timeZoneName != null && !timeZoneName.trim().isEmpty()) {
			timezone = Timezone.byName(timeZoneName); 
		}
		
		timezone = timezone != null ? timezone : Timezone.UTC;
		
		screenshotDelayMS = screenshotDelaySecond * 1000;
		
		
		ajaxResourceTimeout = screenshotDelayMS > ajaxResourceTimeout ? screenshotDelayMS : ajaxResourceTimeout;

		resolutions = resolution.split("\\*");
		
		LinkedHashMap<String, String> map = new LinkedHashMap<>(1);

		map.put("appKey", twAppKey);
		
		Dimension dim = new Dimension(Integer.parseInt(resolutions[0]), Integer.parseInt(resolutions[1]));
		
		Settings sett;
		
		if (usefontconfig)
		{
			sett = Settings.builder().timezone(timezone).requestHeaders(new RequestHeaders(map)).screen(dim).blockAds(false)
				.quickRender(false).ajaxWait(10000).ajaxResourceTimeout(ajaxResourceTimeout).hostnameVerification(false).ssl("trustanything").build();
		}
		else
		{
			 sett = Settings.builder().timezone(timezone).requestHeaders(new RequestHeaders(map)).screen(dim).blockAds(false)
						.quickRender(false).ajaxWait(10000).ajaxResourceTimeout(ajaxResourceTimeout).hostnameVerification(false).ssl("trustanything").javaOptions("-Dprism.useFontConfig=false").build();
		}

		
		JBrowserDriver driver = new JBrowserDriver(sett);		

		
		// This will block for the page load and any
		// associated AJAX requests

		driver.get(server);		
		
		String str_PageSource = driver.getPageSource();
		if (str_PageSource.indexOf("<html><head></head><body></body></html>") != -1) 
		{
			result = "Error: invalid page URL input. The ServerAddress is: " + server;
		} 
		else if (str_PageSource.indexOf("HTTP Status 401 - Authentication failed") != -1)
		{
			result = "HTTP Status 401 - Authentication failed";
		}
		else
		{
			// You can get status code unlike other Selenium drivers.
			// It blocks for AJAX requests and page loads after clicks
			// and keyboard events.

			if(screenshotDelayMS > 0) {
				Thread.sleep(screenshotDelayMS);				
			}
			
			File fil = driver.getScreenshotAs(OutputType.FILE);

			FileRepositoryThing filerepo = (FileRepositoryThing) ThingUtilities.findThing(fileRepository);
			
			filerepo.processServiceRequest("GetDirectoryStructure", null);

			Document document = new Document(PageSize.A4, 10, 10, 10, 10);
			PdfWriter.getInstance(document, new FileOutputStream(filerepo.getRootPath() + File.separator + fileName));
			document.open();
			Image img = Image.getInstance(fil.getAbsolutePath());
			float scaler;
			if (rotate90Deg == true) 
			{
				img.setRotationDegrees(90);
				scaler = (float) (((document.getPageSize().getHeight() - document.leftMargin() - document.rightMargin()
						- 0) / (img.getWidth())) * 100);
			} 
			else 
			{
				scaler = (float) (((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()
						- 0) / (img.getWidth())) * 100);
			}

			img.scalePercent(scaler);
			
			document.add(img);
			document.close();
			result="Success.";
		}
		driver.quit();
		
		return result;

	}
	
	@ThingworxServiceDefinition(name = "MergePDFs", description = "Takes an InfoTable of PDF filenames in the given FileRepository and merges them into a single PDF.")
	@ThingworxServiceResult(name = "Result", description = "Contains error message in case of error.", baseType = "STRING")
	public String MergePDFs(
			@ThingworxServiceParameter(name = "Filenames", description = "List of PDF filenames to merge together.", baseType = "INFOTABLE", aspects = {"dataShape:GenericStringList"}) 
			InfoTable filenames, 
			@ThingworxServiceParameter(name = "OutputFileName", description = "Name of the merged PDF.", baseType = "STRING") 
			String OutputFileName, 
			@ThingworxServiceParameter(name = "FileRepository", description = "The name of the file repository to use.", baseType = "THINGNAME", aspects = {"defaultValue:SystemRepository", "thingTemplate:FileRepository"}) 
			String FileRepository
	)
	{
		String str_Result = "Success";
		Document document = null;
		OutputStream out = null;
		
		//Get the File Repository
		FileRepositoryThing filerepo = (FileRepositoryThing) ThingUtilities.findThing(FileRepository);	
		try 
		{
			filerepo.processServiceRequest("GetDirectoryStructure", null);
			//Set up the output stream for the merged PDF
			out = new FileOutputStream(new File(filerepo.getRootPath() + File.separator + OutputFileName));
			document = new Document(PageSize.A4, 10, 10, 10, 10);
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.open();
			PdfContentByte cb = writer.getDirectContent();
			//Loop through the given PDFs that will be merged
			for(ValueCollection row : filenames.getRows())
	        {
	            String filename = row.getStringValue("item");
	            InputStream is = new FileInputStream(new File(filerepo.getRootPath() + File.separator + filename));
	            PdfReader reader = new PdfReader(is);
	            //Write the pages from the to-be-merged PDFs to the Output PDF
	            for(int i = 1; i <= reader.getNumberOfPages(); i++)
	            {
	            	document.newPage();
	            	PdfImportedPage page = writer.getImportedPage(reader, i);
	            	cb.addTemplate(page, 0, 0);
	            }
	        }	
		} 
		catch (FileNotFoundException e) 
		{
			str_Result = "Unable to create output file.";
			_logger.error(str_Result, e);
		} 
		catch (Exception e) 
		{
			str_Result = "Unable to Get Directory Structure of File Repository: " + FileRepository;
			_logger.error(str_Result, e);
		}
		finally
		{
			try
			{
				//close all the output streams
				out.flush();
				document.close();
				out.close();
			}
			catch (IOException e)
			{
				str_Result = "Unable to write PDF and close OutputStreams.";
				_logger.error(str_Result, e);
			}
		}
		
		return str_Result;	
	}

	@ThingworxServiceDefinition(name = "GetAvailableTimezones", description = "This service will retrieve the available timezone that can be used in the CreatePDF service", category = "PDFExport", isAllowOverride = false, aspects = { "isAsync:false" })
	@ThingworxServiceResult(name = "result", description = "List of available timezone that can be passed to the CreatePDF Service", baseType = "INFOTABLE", aspects = {"isEntityDataShape:true", "dataShape:PDFExport_GetAvailableTimezones" })
	public InfoTable GetAvailableTimezones() {
		// Variable Declaration
		InfoTable result = null; 
		Set<Timezone> timezones;
		
		try {
			result = InfoTableInstanceFactory.createInfoTableFromDataShape(DATA_SHAPE_AVAILABLE_TIME_ZONE);
			timezones = Timezone.ALL_ZONES;
			
			// Loop through all time zone and add it into the value collection
			for(Timezone timezone : timezones) {
				ValueCollection valueCollection = new ValueCollection();
				valueCollection.put(COLUMN_TIME_ZONE_NAME, new StringPrimitive(timezone.name()));
				result.addRow(valueCollection);
			}
			
			result.quickSort(COLUMN_TIME_ZONE_NAME);
			
		}catch(Exception e) {
			result = null;
			_logger.error(e.getMessage());
		}
		
		return result;
	}
}
