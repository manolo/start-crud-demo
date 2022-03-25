package com.example.application.views.crudbooks;

import com.example.application.data.entity.SampleBook;
import com.example.application.data.service.SampleBookService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.component.crud.CrudGrid;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import elemental.json.Json;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.util.UriUtils;

@PageTitle("Crud Books")
@Route(value = "crud4/:sampleBookID?/:action?(edit)")
public class CrudBooksView extends Div {

    // BeforeEnterObserver (edit item by URL) id broken in vaadin-crud
    // https://github.com/vaadin/flow-components/issues/2815
    // private final String SAMPLEBOOK_ID = "sampleBookID";
    // private final String SAMPLEBOOK_EDIT_ROUTE_TEMPLATE = "crud4/%s/edit";

    private Crud<SampleBook> crud;
    private Upload image;
    private Image imagePreview;
    private TextField name;
    private TextField author;
    private DatePicker publicationDate;
    private TextField pages;
    private TextField isbn;

    private FormLayout form;
    private BeanValidationBinder<SampleBook> binder;
    private SampleBookService sampleBookService;

    public CrudBooksView(@Autowired SampleBookService sampleBookService) {
        this.sampleBookService = sampleBookService;
        addClassNames("crud-books-view", "flex", "flex-col", "h-full");

        // Configure Form
        binder = new BeanValidationBinder<>(SampleBook.class);
        // Create Form content
        createEditorLayout();
        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(pages).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("pages");

        binder.bindInstanceFields(this);

        crud = new Crud<>(SampleBook.class, new CrudGrid<>(SampleBook.class, false),
                new BinderCrudEditor<>(binder, form));

        crud.setDataProvider(new AbstractBackEndDataProvider<SampleBook, CrudFilter>() {
            protected Stream<SampleBook> fetchFromBackEnd(Query<SampleBook, CrudFilter> query) {
                return sampleBookService.list(PageRequest.of(query.getPage(), query.getPageSize(),
                        VaadinSpringDataHelpers.toSpringDataSort(query))).stream();
            }

            protected int sizeInBackEnd(Query<SampleBook, CrudFilter> query) {
                return sampleBookService.count();
            }
        });
        crud.addDeleteListener(e -> {
            sampleBookService.delete(e.getItem().getId());
        });
        crud.addSaveListener(e -> {
            SampleBook value = e.getItem();
            value.setImage(imagePreview.getSrc());

            sampleBookService.update(value);
        });
        crud.addEditListener(e -> {
            SampleBook value = e.getItem();
            this.imagePreview.setVisible(value != null);
            if (value == null) {
                this.imagePreview.setSrc("");
            } else {
                this.imagePreview.setSrc(value.getImage());
            }

        });
        attachImageUpload(image, imagePreview);

        crud.setHeightFull();
        crud.setEditOnClick(true);
        add(crud);
    }

    private void createEditorLayout() {
        Label imageLabel = new Label("Image");
        imagePreview = new Image();
        imagePreview.setWidth("100%");
        image = new Upload();
        image.getStyle().set("box-sizing", "border-box");
        image.getElement().appendChild(imagePreview.getElement());
        name = new TextField("Name");
        author = new TextField("Author");
        publicationDate = new DatePicker("Publication Date");
        pages = new TextField("Pages");
        isbn = new TextField("Isbn");
        Component[] fields = new Component[]{imageLabel, image, name, author, publicationDate, pages, isbn};

        form = new FormLayout(fields);
        attachImageUpload(image, imagePreview);

    }

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/*");
        upload.setReceiver((fileName, mimeType) -> {
            return uploadBuffer;
        });
        upload.addSucceededListener(e -> {
            String mimeType = e.getMIMEType();
            String base64ImageData = Base64.getEncoder().encodeToString(uploadBuffer.toByteArray());
            String dataUrl = "data:" + mimeType + ";base64,"
                    + UriUtils.encodeQuery(base64ImageData, StandardCharsets.UTF_8);
            upload.getElement().setPropertyJson("files", Json.createArray());
            preview.setSrc(dataUrl);
            uploadBuffer.reset();
        });
        preview.setVisible(false);
    }

}
