package org.abx.controller;

import org.abx.model.Campaign;
import org.abx.controller.util.JsfUtil;
import org.abx.controller.util.ReportItem;

//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.Closeable;
//import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
//import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.abx.model.CampaignData;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.DbxWriteMode;

@Named("campaignController")
@SessionScoped
public class CampaignController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Campaign current;
	private LazyDataModel<Campaign> items = null;
	@EJB
	private org.abx.controller.CampaignFacade ejbFacade;

	private int selectedItemIndex;

	@Inject
	ServletContext svrcontext;

	@EJB
	private org.abx.controller.CampaignDataFacade ejbCampaignDataFacade;

	public CampaignController() {
	}

	public Campaign getSelected() {
		if (current == null) {
			current = new Campaign();
			selectedItemIndex = -1;
		}
		return current;
	}

	private CampaignFacade getFacade() {
		return ejbFacade;
	}

	public String prepareList() {
		recreateModel();
		return "List";
	}

	public String prepareView() {
		current = (Campaign) getItems().getRowData();

		return "View";
	}

	public String prepareCreate() {
		current = new Campaign();
		selectedItemIndex = -1;
		return "Create";
	}

	public String create() {
		try {
			getFacade().create(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("Bundle")
					.getString("CampaignCreated"));
			return prepareCreate();
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("Bundle")
					.getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String prepareEdit() {
		current = (Campaign) getItems().getRowData();

		return "Edit?faces-redirect=true";
	}

	DbxAuthFinish dbxAuth = null;

	private String getUrl(HttpServletRequest request, String path)
			throws MalformedURLException {
		URL requestUrl;
		requestUrl = new URL(request.getRequestURL().toString());
		return new URL(requestUrl, path).toExternalForm();

	}

	private DbxWebAuth getWebAuth(final HttpServletRequest request)
			throws MalformedURLException {
		// After we redirect the user to the Dropbox website for authorization,
		// Dropbox will redirect them back here.
		String redirectUrl = getUrl(request,
				"/marketingreports/faces/campaign/Edit.xhtml").replace("http://","https://");

		// Select a spot in the session for DbxWebAuth to store the CSRF token.
		HttpSession session = request.getSession(true);
		String sessionKey = "dropbox-auth-csrf-token";
		DbxSessionStore csrfTokenStore = new DbxStandardSessionStore(session,
				sessionKey);

		DbxAppInfo appInfo = new DbxAppInfo("n1a7mjuo3j6fsai",
				"efedqniedh4t290");
		DbxRequestConfig config = new DbxRequestConfig("MarketingReports",
				Locale.getDefault().toString());

		return new DbxWebAuth(config, appInfo, redirectUrl, csrfTokenStore);
	}

	public void handleEvent(ComponentSystemEvent event) {
		try {
			HttpServletRequest req = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();

			DbxWebAuth auth = getWebAuth(req);
			if (req.getParameterMap().containsKey("code") == true) {

				dbxAuth = auth.finish(req.getParameterMap());
				isAuthAvailable = false;
				isUploadAvailable = true;
			}

		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("Bundle")
					.getString("PersistenceErrorOccured"));

		}
	}

	Boolean isUploadAvailable = false;

	public Boolean getIsUploadAvailable() {
		return isUploadAvailable;
	}

	Boolean isAuthAvailable = true;

	public Boolean getIsAuthAvailable() {
		return isAuthAvailable;
	}

	public void callAuth() {
		try {

			if (dbxAuth != null)
				return;

			HttpServletRequest req = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();

			DbxWebAuth auth = getWebAuth(req);
			if (req.getParameterMap().containsKey("code") == false) {

				String url = auth.start();
				FacesContext.getCurrentInstance().getExternalContext()
						.redirect(url);
			}

		} catch (Exception ex) {
			JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("Bundle")
					.getString("PersistenceErrorOccured"));
		}

	}

	public String update() {

		try {

			Boolean hasFile = false;
			if (current.getFile() != null) {
				String fileName = current.getFile().getFileName();
				current.setFileName(fileName);
				hasFile = true;
			}

			getFacade().edit(current);

			if (hasFile == true) {
				try {
					upload();
				} catch (IOException e) {
					JsfUtil.addErrorMessage(
							e,
							ResourceBundle.getBundle("Bundle").getString(
									"PersistenceErrorOccured"));
					return null;
				}
			}

			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("Bundle")
					.getString("CampaignUpdated"));
			return "View";
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("Bundle")
					.getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String destroy() {
		current = (Campaign) getItems().getRowData();

		performDestroy();
		recreateModel();
		return "List";
	}

	public String destroyAndView() {
		performDestroy();
		recreateModel();
		updateCurrentItem();
		if (selectedItemIndex >= 0) {
			return "View";
		} else {
			// all items were removed - go back to list
			recreateModel();
			return "List";
		}
	}

	private void performDestroy() {
		try {
			getFacade().remove(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("Bundle")
					.getString("CampaignDeleted"));
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("Bundle")
					.getString("PersistenceErrorOccured"));
		}
	}

	private void updateCurrentItem() {
		int count = getFacade().count();
		if (selectedItemIndex >= count) {
			// selected index cannot be bigger than number of items:
			selectedItemIndex = count - 1;
			// go to previous page if last page disappeared:

		}
		if (selectedItemIndex >= 0) {
			current = getFacade().findRange(
					new int[] { selectedItemIndex, selectedItemIndex + 1 })
					.get(0);
		}
	}

	public LazyDataModel<Campaign> getItems() {
		if (items == null) {
			items = new LazyDataModel<Campaign>() {
				private static final long serialVersionUID = 1L;

				@Override
				public List<Campaign> load(int first, int pageSize,
						String sortField, SortOrder sortOrder,
						Map<String, Object> filters) {
					List<Campaign> result = getFacade().getFilterResultList(
							first, pageSize, sortField, sortOrder, filters);
					this.setRowCount(getFacade().getFilterResultCount(
							sortField, sortOrder, filters));
					return result;
				}
			};
			items.setRowCount(getFacade().count());
		}
		return items;
	}

	private void recreateModel() {
		items = null;
	}

	public SelectItem[] getItemsAvailableSelectMany() {
		return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
	}

	public Campaign getCampaign(java.lang.Integer id) {
		return ejbFacade.find(id);
	}

	private String getUploadedFilename() {

		String name = null;
		String[] temps = current.getFile().getFileName().replace('\\', '/')
				.split("/");
		for (String temp : temps) {
			name = temp;
		}
		return name;
	}

	public void upload() throws IOException, DbxException {

		DbxRequestConfig config = new DbxRequestConfig("MarketingReports",
				Locale.getDefault().toString());

		DbxClient client = new DbxClient(config, dbxAuth.accessToken);

		String fullTargetPath = "/" + getUploadedFilename();
		DbxEntry.File metadata;

		metadata = client
				.uploadFile(fullTargetPath, DbxWriteMode.add(), current
						.getFile().getSize(), current.getFile()
						.getInputstream());

		// try (InputStream inputStream = current.getFile().getInputStream()) {
		// String finalPath = svrcontext.getRealPath("/Temp/"
		// + current.getFileName());
		// try (FileOutputStream outputStream = new FileOutputStream(finalPath))
		// {
		// byte[] buffer = new byte[4096];
		// int bytesRead;
		// while (true) {
		// bytesRead = inputStream.read(buffer);
		// if (bytesRead > 0) {
		// outputStream.write(buffer, 0, bytesRead);
		// } else {
		// break;
		// }
		// }
		// }
		// }
	}

	public String generateReport2() throws SQLException, JRException,
			IOException, NamingException {

		// insertFile();

		current = (Campaign) getItems().getRowData();
		ArrayList<ReportItem> arrlist = createReportItems();

		String reportPath = svrcontext
				.getRealPath("/Reports/QAPercentageReport.jrxml");
		JasperReport jasperReport = JasperCompileManager
				.compileReport(reportPath);

		HashMap<String, Object> parameters = new HashMap<String, Object>();

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
				parameters, new JRBeanCollectionDataSource(arrlist));

		// String reportPdfPath =
		// svrcontext.getRealPath("/Temp/temp_report.pdf");
		String reportPdfPath = System.getProperty("java.io.tmpdir")
				+ "/temp_report.pdf";
		JasperExportManager.exportReportToPdfFile(jasperPrint, reportPdfPath);

		ExternalContext context = FacesContext.getCurrentInstance()
				.getExternalContext();
		HttpServletResponse response = (HttpServletResponse) context
				.getResponse(); // /HttpServletResponse is not being found...
		// response.sendRedirect("../Temp/temp_report.pdf");
		response.sendRedirect(reportPdfPath);
		return "List";
	}
	
	public void generateReport(Campaign selectedCampaign) {
		try {
			current = selectedCampaign;

			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("jdbc/mydb");

			String reportPath = svrcontext
					.getRealPath("/Reports/CampaingReport.jrxml");
			JasperReport jasperReport = JasperCompileManager
					.compileReport(reportPath);

			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("CampaignID", current.getCampaignId());
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport, parameters, ds.getConnection());

			String pdfReportFileName = "temp_report"
					+ UUID.randomUUID().toString() + ".pdf";
			String reportPdfPath = System.getProperty("java.io.tmpdir") + "/"
					+ pdfReportFileName;
			JasperExportManager.exportReportToPdfFile(jasperPrint,
					reportPdfPath);

			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("id", pdfReportFileName);
			return;

			// Map<String, Object> options = new HashMap<String, Object>();
			// options.put("modal", true);
			// options.put("draggable", false);
			// options.put("resizable", false);
			// options.put("contentHeight", 320);
			//
			// RequestContext.getCurrentInstance().openDialog("reportViewer",
			// options,
			// null);

		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("Bundle")
					.getString("ReportErrorOccured"));

		}

	}

	ArrayList<ReportItem> createReportItems() throws FileNotFoundException,
			IOException {

		// String filePath = svrcontext.getRealPath("/Temp/" +
		// current.getFileName());
		String filePath = System.getProperty("java.io.tmpdir")
				+ current.getFileName();
		FileInputStream input = new FileInputStream(filePath);

		ArrayList<HashMap<String, ReportItem>> categoryList = null;
		try {
			if (filePath.endsWith(".xlsx") == true) {
				XSSFWorkbook wb = new XSSFWorkbook(input);
				XSSFSheet sheet = wb.getSheetAt(0);

				XSSFRow row, header;
				header = sheet.getRow(0);

				categoryList = new ArrayList<HashMap<String, ReportItem>>();

				for (int j = 0; j < header.getLastCellNum(); j++) {

					HashMap<String, ReportItem> map = new HashMap<String, ReportItem>();
					categoryList.add(map);

					String category = header.getCell(j).getStringCellValue();

					CTCell cell = header.getCell(j).getCTCell();
					if (cell == null) // || cell.getR().equals("BX"))
						break;

					for (int i = 1; i < 484; i++) {
						row = sheet.getRow(i);

						XSSFCell cellv = row.getCell(j);
						String descr = null;
						if (cellv.getCellType() == 3)
							descr = "N/A";
						else if (cellv.getCellType() == 1)
							descr = cellv.getStringCellValue();
						else if (cellv.getCellType() == 0)
							descr = String.valueOf(new Double(cellv
									.getNumericCellValue()).intValue());
						else
							descr = "N/A";

						ReportItem item = null;
						if (!map.containsKey(descr)) {
							item = new ReportItem();
							item.setDescription(descr);
							item.setCategory(category);
							item.setCount(1);
							map.put(descr, item);
						} else {
							item = map.get(descr);
							item.setCount(item.getCount() + 1);
						}

					}
				}

			}

			ArrayList<ReportItem> list = new ArrayList<ReportItem>();
			for (int i = 0; i < categoryList.size(); i++) {
				HashMap<String, ReportItem> map = categoryList.get(i);
				ReportItem[] arr = new ReportItem[map.size()];
				map.values().toArray(arr);
				for (int j = 0; j < map.size(); j++) {
					list.add(arr[j]);
				}

			}
			return list;

		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("Bundle")
					.getString("PersistenceErrorOccured"));
			return null;
		}

	}

	public String importExcelFile() throws FileNotFoundException, IOException {
		current = (Campaign) getItems().getRowData();

		// String filePath = svrcontext.getRealPath("/Temp/"+
		// current.getFileName());
		String filePath = System.getProperty("java.io.tmpdir")
				+ current.getFileName();
		FileInputStream input = new FileInputStream(filePath);

		try {
			if (filePath.endsWith(".xlsx") == true) {
				XSSFWorkbook wb = new XSSFWorkbook(input);
				XSSFSheet sheet = wb.getSheetAt(0);

				XSSFRow row;
				for (int i = 1; i <= sheet.getLastRowNum(); i++) {
					CampaignData camp_data = new CampaignData();
					row = sheet.getRow(i);
					Double dtemp = row.getCell(0).getNumericCellValue();

					camp_data.setNoWeek(dtemp.intValue());
					camp_data.setWeekDescr(row.getCell(1).getStringCellValue());
					camp_data.setMedium(row.getCell(2).getStringCellValue());
					camp_data.setAdType(row.getCell(3).getStringCellValue());
					camp_data.setCountry(row.getCell(4).getStringCellValue());

					dtemp = row.getCell(6).getNumericCellValue();
					camp_data.setImpressions(dtemp.intValue());

					dtemp = row.getCell(7).getNumericCellValue();
					camp_data.setClicks(dtemp.intValue());

					dtemp = row.getCell(8).getNumericCellValue();
					camp_data.setCost(dtemp);
					camp_data.setCampaignId(current);

					ejbCampaignDataFacade.edit(camp_data);
				}
			} else {

				POIFSFileSystem fs = new POIFSFileSystem(input);
				HSSFWorkbook wb = new HSSFWorkbook(fs);
				HSSFSheet sheet = wb.getSheetAt(0);
				Row row;
				for (int i = 1; i <= sheet.getLastRowNum(); i++) {
					CampaignData camp_data = new CampaignData();
					row = sheet.getRow(i);
					Double dtemp = row.getCell(0).getNumericCellValue();

					camp_data.setNoWeek(dtemp.intValue());
					camp_data.setWeekDescr(row.getCell(1).getStringCellValue());
					camp_data.setMedium(row.getCell(2).getStringCellValue());
					camp_data.setAdType(row.getCell(3).getStringCellValue());
					camp_data.setCountry(row.getCell(4).getStringCellValue());

					dtemp = row.getCell(6).getNumericCellValue();
					camp_data.setImpressions(dtemp.intValue());

					dtemp = row.getCell(7).getNumericCellValue();
					camp_data.setClicks(dtemp.intValue());

					dtemp = row.getCell(8).getNumericCellValue();
					camp_data.setCost(dtemp);
					camp_data.setCampaignId(current);

					ejbCampaignDataFacade.edit(camp_data);
				}
			}

			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("Bundle")
					.getString("CampaignDataUpdated"));
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("Bundle")
					.getString("PersistenceErrorOccured"));
		}

		return "List";
	}

	@FacesConverter(forClass = Campaign.class)
	public static class CampaignControllerConverter implements Converter {

		@Override
		public Object getAsObject(FacesContext facesContext,
				UIComponent component, String value) {
			if (value == null || value.length() == 0) {
				return null;
			}
			CampaignController controller = (CampaignController) facesContext
					.getApplication()
					.getELResolver()
					.getValue(facesContext.getELContext(), null,
							"campaignController");
			return controller.getCampaign(getKey(value));
		}

		java.lang.Integer getKey(String value) {
			java.lang.Integer key;
			key = Integer.valueOf(value);
			return key;
		}

		String getStringKey(java.lang.Integer value) {
			StringBuilder sb = new StringBuilder();
			sb.append(value);
			return sb.toString();
		}

		@Override
		public String getAsString(FacesContext facesContext,
				UIComponent component, Object object) {
			if (object == null) {
				return null;
			}
			if (object instanceof Campaign) {
				Campaign o = (Campaign) object;
				return getStringKey(o.getCampaignId());
			} else {
				throw new IllegalArgumentException("object " + object
						+ " is of type " + object.getClass().getName()
						+ "; expected type: " + Campaign.class.getName());
			}
		}

	}

}
