package com.reisystems.hackathon.earthday2016.controller;

import com.reisystems.hackathon.earthday2016.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/fpds")
@Api(tags = "FPDS", description = "Federal Procurement Data System")
public class FpdsController {

    @RequestMapping(value = "/spending", method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    @ApiOperation(value = "TotalSpending Federal Procurement")
    public HttpEntity getTotal() {
        TotalSpending totalSpending = new TotalSpending();
        totalSpending.setAmount(1000000000.0);
        totalSpending.setAmountSustainable(340000000.0);

        return ResponseEntity.ok().body(totalSpending);
    }

    @RequestMapping(value = "/sustainability/agencies", method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    @ApiOperation(value = "Spending by Agencies")
    public HttpEntity getSpendingByAgencies(
            @RequestParam(value = "limit", required = false) Integer limit) {

        if (limit == null) {
            limit = 10;
        }

        List<ContextBasedSpending> agencies = new ArrayList<>();

        ContextBasedSpending agency;
        for (int i = 1; i <= limit; i++) {
            agency = new ContextBasedSpending();
            agency.setIdentifier("1400" + Integer.toString(i));
            agency.setAcronym("A" + Integer.toString(i));
            agency.setName("Context " + Integer.toString(i));
            agency.setAmount(12300678.0 / i);
            agency.setAmountSustainable(50000.0 / i);
            agencies.add(agency);
        }

        List<Link> links = new ArrayList<>();

        return ResponseEntity.ok().body(new Resources<>(agencies, links));
    }

    @RequestMapping(value = "/sustainability/agency/trend", method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    @ApiOperation(value = "Spending by Agencies")
    public HttpEntity getAgencySpendingTrend() {
        List<TotalSpending> trend = new ArrayList<>();

        Trend spending;
        for (int i = 1; i <= 10; i++) {
            spending = new Trend();
            spending.setDate(new Date(2005 + i, 1, 1));
            spending.setAmount(1678.5 * i);
            spending.setAmountSustainable(26.0 * i);
            trend.add(spending);
        }

        List<Link> links = new ArrayList<>();

        return ResponseEntity.ok().body(new Resources<>(trend, links));
    }

    @RequestMapping(value = "/sustainability/states", method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    @ApiOperation(value = "Spending by States")
    public HttpEntity getSpendingByStates() {
        List<ContextBasedSpending> states = new ArrayList<>();

        ContextBasedSpending state = new ContextBasedSpending();
        state.setIdentifier("1400");
        state.setAcronym("VA");
        state.setName("Virginia");
        state.setAmount(100000.0);
        state.setAmountSustainable(1000.0);
        states.add(state);

        ContextBasedSpending state2 = new ContextBasedSpending();
        state2.setIdentifier("1789");
        state2.setAcronym("MD");
        state2.setName("Maryland");
        state2.setAmount(1000000.0);
        state2.setAmountSustainable(1000.0);
        states.add(state2);

        List<Link> links = new ArrayList<>();

        return ResponseEntity.ok().body(new Resources<>(states, links));
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    @ApiOperation(value = "Transactions")
    public HttpEntity getTransactions(
        @RequestParam(value = "agencyId", required = false) String agencyId,
        @RequestParam(value = "offset", required = false) Integer offset,
        @RequestParam(value = "limit", required = false) Integer limit) {

        if (limit == null) {
            limit = 10;
        }

        Map<String, Object> query = new HashMap<>();
        if (agencyId != null) {
            query.put("agency_id", agencyId);
        }

        List<Transaction> transactions = new ArrayList<>();

        Transaction transaction;
        for (int i = 0; i < limit; i++) {
            transaction = new Transaction();
            transaction.setIdentifier(Integer.toString(10 + i));
            transaction.setAgencyName("Agency " + Integer.toString(i));
            transaction.setProductName("Product " + Integer.toString(i + 34));
            transaction.setState("State " + Integer.toString(i + 5));
            transaction.setAmount(123.0 * i);
            transaction.setDate(new Date(2015, i, 1));
            transactions.add(transaction);
        }

        if (transactions != null) {
            for (Transaction t: transactions) {
            }
        }

        List<Link> links = new ArrayList<>();
        FpdsController builder = methodOn(FpdsController.class);

        // self
        links.add(linkTo(builder.getTransactions(agencyId, offset, limit)).withSelfRel());
        // navigation links
        if ((offset != null) && (offset > 0)) {
            links.add(linkTo(builder.getTransactions(agencyId, null, limit)).withRel(Link.REL_FIRST));
        }
        if (limit != null) {
            if ((transactions.size() == limit)) {
                Integer nextOffset = ((offset == null) ? 0 : offset) + limit;
                links.add(linkTo(builder.getTransactions(agencyId, nextOffset, limit)).withRel(Link.REL_NEXT));
            }
            if ((offset != null) && (offset > 0)) {
                Integer prevOffset = (offset > limit) ? offset - limit : null;
                links.add(linkTo(builder.getTransactions(agencyId, prevOffset, limit)).withRel(Link.REL_PREVIOUS));
            }
        }
        // search
        ControllerLinkBuilder searchLinkBuilder = linkTo(builder.getTransactions(null, null, null));
        Link searchLink = new Link(searchLinkBuilder.toString() + "{agencyId,offset,limit}", "search");
        links.add(searchLink);

        return ResponseEntity.ok().body(new Resources<>(transactions, links));
    }
}
