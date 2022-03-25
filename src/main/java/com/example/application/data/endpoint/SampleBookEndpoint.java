package com.example.application.data.endpoint;

import com.example.application.data.entity.SampleBook;
import com.example.application.data.service.SampleBookService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Endpoint
@AnonymousAllowed
public class SampleBookEndpoint {

    private SampleBookService service;

    public SampleBookEndpoint(@Autowired SampleBookService service) {
        this.service = service;
    }

    @Nonnull
    public Page<@Nonnull SampleBook> list(Pageable page) {
        return service.list(page);
    }

    public Optional<SampleBook> get(@Nonnull UUID id) {
        return service.get(id);
    }

    @Nonnull
    public SampleBook update(@Nonnull SampleBook entity) {
        return service.update(entity);
    }

    public void delete(@Nonnull UUID id) {
        service.delete(id);
    }

    public int count() {
        return service.count();
    }

}
