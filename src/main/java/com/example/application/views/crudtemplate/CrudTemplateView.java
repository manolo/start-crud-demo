package com.example.application.views.crudtemplate;

import com.example.application.data.entity.SamplePerson;
import com.example.application.data.service.SamplePersonService;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.component.crud.CrudGrid;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Crud Template")
@Route(value = "crud2/:samplePersonID?/:action?(edit)")
@Tag("crud-template-view")
@JsModule("./views/crudtemplate/crud-template-view.ts")
@Uses(Icon.class)
public class CrudTemplateView extends LitTemplate implements HasStyle {

    // BeforeEnterObserver (edit item by URL) id broken in vaadin-crud
    // https://github.com/vaadin/flow-components/issues/2815
    // private final String SAMPLEPERSON_ID = "samplePersonID";
    // private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "crud2/%s/edit";

    // This is the Java companion file of a design
    // You can find the design file inside /frontend/views/
    // The design can be easily edited by using Vaadin Designer
    // (vaadin.com/designer)

    @Id
    private Crud<SamplePerson> crud;
    @Id
    private FormLayout form;
    @Id
    private TextField firstName;
    @Id
    private TextField lastName;
    @Id
    private TextField email;
    @Id
    private TextField phone;
    @Id
    private DatePicker dateOfBirth;
    @Id
    private TextField occupation;
    @Id
    private Checkbox important;

    private BeanValidationBinder<SamplePerson> binder;
    private SamplePersonService samplePersonService;

    public CrudTemplateView(@Autowired SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;

        addClassNames("crud-template-view", "flex", "flex-col", "h-full");

        // Configure Form
        binder = new BeanValidationBinder<>(SamplePerson.class);
        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        crud.setBeanType(SamplePerson.class);
        crud.setGrid(new CrudGrid<>(SamplePerson.class, false));
        crud.setEditor(new BinderCrudEditor<>(binder, form));

        crud.setDataProvider(new AbstractBackEndDataProvider<SamplePerson, CrudFilter>() {
            protected Stream<SamplePerson> fetchFromBackEnd(Query<SamplePerson, CrudFilter> query) {
                return samplePersonService.list(PageRequest.of(query.getPage(), query.getPageSize(),
                        VaadinSpringDataHelpers.toSpringDataSort(query))).stream();
            }

            protected int sizeInBackEnd(Query<SamplePerson, CrudFilter> query) {
                return samplePersonService.count();
            }
        });
        crud.addDeleteListener(e -> {
            samplePersonService.delete(e.getItem().getId());
        });
        crud.addSaveListener(e -> {
            SamplePerson value = e.getItem();

            samplePersonService.update(value);
        });
        crud.addEditListener(e -> {
            SamplePerson value = e.getItem();

        });

        crud.setHeightFull();
        crud.setEditOnClick(true);
    }

}
