package com.example.application.views.crudjava;

import com.example.application.data.entity.SamplePerson;
import com.example.application.data.service.SamplePersonService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.component.crud.CrudGrid;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
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

@PageTitle("Crud Java")
@Route(value = "crud/:samplePersonID?/:action?(edit)")
@Uses(Icon.class)
public class CrudJavaView extends Div {

    // BeforeEnterObserver (edit item by URL) id broken in vaadin-crud
    // https://github.com/vaadin/flow-components/issues/2815
    // private final String SAMPLEPERSON_ID = "samplePersonID";
    // private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "crud/%s/edit";

    private Crud<SamplePerson> crud;
    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField phone;
    private DatePicker dateOfBirth;
    private TextField occupation;
    private Checkbox important;

    private FormLayout form;
    private BeanValidationBinder<SamplePerson> binder;
    private SamplePersonService samplePersonService;

    public CrudJavaView(@Autowired SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;
        addClassNames("crud-java-view", "flex", "flex-col", "h-full");

        // Configure Form
        binder = new BeanValidationBinder<>(SamplePerson.class);
        // Create Form content
        createEditorLayout();
        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        crud = new Crud<>(SamplePerson.class, new CrudGrid<>(SamplePerson.class, false),
                new BinderCrudEditor<>(binder, form));

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
        add(crud);
    }

    private void createEditorLayout() {
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        email = new TextField("Email");
        phone = new TextField("Phone");
        dateOfBirth = new DatePicker("Date Of Birth");
        occupation = new TextField("Occupation");
        important = new Checkbox("Important");
        Component[] fields = new Component[]{firstName, lastName, email, phone, dateOfBirth, occupation, important};

        form = new FormLayout(fields);

    }

}
