#include "playlist_tree.hpp"

using namespace staccato;

PlaylistTree::PlaylistTree() {}

std::vector<std::any>* PlaylistTree::find_folder(const std::vector<std::string>& folder_hierarchy) {

    std::vector<std::any>* folder = &root;
    for(const std::string& folder_name: folder_hierarchy) {

        bool found = false;
        for(std::any& item: *folder) {

            //Each item can be a std::pair<std::string, std::vector<std::any>> (representing folders) or a std::string (representing playlist IDs) 
            if(item.type() != typeid(std::pair<std::string, std::vector<std::any>>)) {

                continue;

            }

            std::pair<std::string, std::vector<std::any>>& child_folder = std::any_cast<std::pair<std::string, std::vector<std::any>>&>(item);
            if(child_folder.first == folder_name) {

                folder = &child_folder.second;
                found = true;
                break;

            }

        }

        if(!found) {

            return nullptr;

        }

    }

    return folder;

}

std::vector<std::string> PlaylistTree::get_folder_playlists_recursive(const std::vector<std::any>& folder) const {

    std::vector<std::string> id_list {};
    //The stack stores playlist folders-- more specifically, pairs of the folder itself and its iterator.
    std::stack<std::pair<const std::vector<std::any>&, std::vector<std::any>::const_iterator>> todo;
    todo.push({folder, folder.begin()});
    while(!todo.empty()) {

        const std::vector<std::any>& folder = todo.top().first;
        std::vector<std::any>::const_iterator& iter = todo.top().second;

        //We're done iterating the current folder:
        if(iter == folder.end()) {

            todo.pop();
            continue;

        }
        
        if((*iter).type() == typeid(std::string)) {

            id_list.push_back(std::any_cast<const std::string&>(*iter));

        } else if((*iter).type() == typeid(std::pair<std::string, std::vector<std::any>>)) {

            const std::vector<std::any>& child_folder = std::any_cast<const std::pair<std::string, std::vector<std::any>>&>(*iter).second;
            todo.push({child_folder, child_folder.begin()});

        } else {

            return {}; //Error encountered due to invalid type

        }

        iter++;

    }

    return id_list;

}

bool PlaylistTree::add_playlist(const std::string& id, const std::vector<std::string>& folder_hierarchy) {

    std::vector<std::any>* folder_ptr = find_folder(folder_hierarchy);
    if(folder_ptr == nullptr) {

        return false;

    }

    (*folder_ptr).push_back(id);

    return true;

}

bool PlaylistTree::add_folder(const std::string& name, const std::vector<std::string>& folder_hierarchy) {

    std::vector<std::any>* folder_ptr = find_folder(folder_hierarchy);
    if(folder_ptr == nullptr) {

        return false;

    }

    (*folder_ptr).push_back(std::pair<std::string, std::vector<std::any>>(name, std::vector<std::any>()));

    return true;

}

bool PlaylistTree::remove_playlist(const std::string& id, const std::vector<std::string>& folder_hierarchy) {

    std::vector<std::any>* folder_ptr = find_folder(folder_hierarchy);
    if(folder_ptr == nullptr) {

        return false;

    }

    std::vector<std::any>& folder = *folder_ptr;
    for(std::size_t i {0}; i < folder.size(); i++) {

        if(folder[i].type() == typeid(std::string) && std::any_cast<const std::string&>(folder[i]) == id) {

            folder.erase(folder.begin() + i);
            return true;

        }

    }

    return false;

}

std::vector<std::string> PlaylistTree::remove_folder(const std::string& name, const std::vector<std::string>& folder_hierarchy) {

    std::vector<std::any>* folder_ptr = find_folder(folder_hierarchy);
    if(folder_ptr == nullptr) {

        return {};

    }

    std::vector<std::any>& folder = *folder_ptr;
    for(std::size_t i {0}; i < folder.size(); i++) {

        if(folder[i].type() == typeid(std::pair<std::string, std::vector<std::any>>)) {

            const std::pair<std::string, std::vector<std::any>>& folder_info = std::any_cast<const std::pair<std::string, std::vector<std::any>>&>(folder[i]);

            if(folder_info.first != name) {

                continue;

            }

            std::vector<std::string> removed_playlists = get_folder_playlists_recursive(folder_info.second);
            folder.erase(folder.begin() + i);
            return removed_playlists;

        }

    }

    return {};

}

std::string PlaylistTree::string() const {

    std::string str {"[root]:\n"};
    int num_indents {0};
    int indent_width {4};
    //The stack stores playlist folders-- more specifically, pairs of the folder itself and its iterator.
    std::stack<std::pair<const std::vector<std::any>&, std::vector<std::any>::const_iterator>> todo;
    todo.push({root, root.begin()});
    while(!todo.empty()) {

        const std::vector<std::any>& folder = todo.top().first;
        std::vector<std::any>::const_iterator& iter = todo.top().second;

        //We're done iterating the current folder:
        if(iter == folder.end()) {

            todo.pop();
            num_indents--;
            continue;

        }
        
        for(int i = 0; i < num_indents * indent_width; i++) {

            str += ' ';

        }

        if((*iter).type() == typeid(std::string)) {

            str += "\"" + std::any_cast<const std::string&>(*iter) + "\"\n";

        } else if((*iter).type() == typeid(std::pair<std::string, std::vector<std::any>>)) {

            for(int i = 0; i < indent_width; i++) {

                str += ' ';

            }

            const std::pair<std::string, std::vector<std::any>>& child_folder = std::any_cast<const std::pair<std::string, std::vector<std::any>>&>(*iter);
            str += child_folder.first + ": \n";
            todo.push({child_folder.second, child_folder.second.begin()});
            num_indents++;

        } else {

            str += "error in type\n";
            return str; //Error encountered due to invalid type

        }

        iter++;

    }

    return str;

}
