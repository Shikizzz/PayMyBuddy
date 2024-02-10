package com.p6.payMyBuddy.services;

import com.p6.payMyBuddy.model.DTO.Connection;
import com.p6.payMyBuddy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ConnectionsService {

    public Page<Connection> findPaginated(Pageable pageable, ArrayList<Connection> connections){
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Connection> list;
        if (connections.size() < startItem) {
            list = Collections.emptyList();
        }
        else{
            int toIndex = Math.min(startItem + pageSize, connections.size());
            list = connections.subList(startItem, toIndex);
        }
        Page<Connection> connectionsPage = new PageImpl<Connection>(list, PageRequest.of(currentPage, pageSize), connections.size());
        return connectionsPage;
    }





    public ArrayList<Connection> userToConnectionList(List<User> users){ //DTO converter
        ArrayList<Connection> connections = new ArrayList<>();
        for(int i=0; i<users.size(); i++){
            Connection connection = new Connection(users.get(i).getEmail(), users.get(i).getFirstName(), users.get(i).getLastName());
            connections.add(connection);
        }
        return connections;
    }
}
