#include "playlist_tree.hpp"
#include <nlohmann/json.hpp>

using namespace staccato;

PlaylistTree::PlaylistTree() {}

PlaylistTree::Iterator::Iterator(std::vector<std::any>& init_folder, std::size_t index) {

    todo_.push({init_folder, index});

}

PlaylistTree::ConstIterator::ConstIterator(const std::vector<std::any>& init_folder, std::size_t index) {

    todo_.push({init_folder, index});

}

PlaylistTree::Iterator& PlaylistTree::Iterator::operator++() {

    if(todo_.empty()) {

        return *this;

    }

    std::vector<std::any>& folder = todo_.top().first;
    std::size_t& index = todo_.top().second;
    
    //If we're at the end of a folder, then we go back to the parent folder
    if(index >= folder.size()) {

        todo_.pop();
        return *this;

    }

    //If the iter. is at a folder, then we go into that child folder next iteration.
    if(folder[index].type() == typeid(std::pair<std::string, std::vector<std::any>>)) {

        std::vector<std::any>& child_folder = std::any_cast<std::pair<std::string, std::vector<std::any>>&>(folder[index]).second;
        todo_.push({child_folder, 0});

    }

    index++; //Increment this folder's index

    return *this;
    
}

PlaylistTree::ConstIterator& PlaylistTree::ConstIterator::operator++() {

    if(todo_.empty()) {

        return *this;

    }

    const std::vector<std::any>& folder = todo_.top().first;
    std::size_t& index = todo_.top().second;

    //If we're at the end of a folder, then we go back to the parent folder
    if(index >= folder.size()) {

        todo_.pop();
        return *this;

    }

    //If the iter. is at a folder, then we go into that child folder next iteration.
    if(folder[index].type() == typeid(std::pair<std::string, std::vector<std::any>>)) {

        const std::vector<std::any>& child_folder = std::any_cast<const std::pair<std::string, std::vector<std::any>>&>(folder[index]).second;
        todo_.push({child_folder, 0});

    }

    index++; //Increment this folder's index

    return *this;
    
}

PlaylistTree::Iterator PlaylistTree::Iterator::operator++(int) {

    //Ok, so now I see why we like using prefix increment.
    //We have to create a copy of this iterator if we use postfix.

    Iterator temp = *this; //Make a copy of the current iterator state
    ++(*this);
    return temp;

}

PlaylistTree::ConstIterator PlaylistTree::ConstIterator::operator++(int) {

    //Ok, so now I see why we like using prefix increment.
    //We have to create a copy of this iterator if we use postfix.

    ConstIterator temp = *this; //Make a copy of the current iterator state
    ++(*this);
    return temp;

}

std::string* PlaylistTree::Iterator::operator->() const {

    std::vector<std::any>& folder = todo_.top().first;
    const std::size_t& index = todo_.top().second;

    if(index >= folder.size()) {

        return nullptr;

    }

    if(folder[index].type() == typeid(std::string)) {

        return std::any_cast<std::string>(&folder[index]);

    } else if(folder[index].type() == typeid(std::pair<std::string, std::vector<std::any>>)) {

        return &std::any_cast<std::pair<std::string, std::vector<std::any>>>(&folder[index])->first;

    } else {

        return nullptr;

    }

}

const std::string* PlaylistTree::ConstIterator::operator->() const {

    const std::vector<std::any>& folder = todo_.top().first;
    const std::size_t& index = todo_.top().second;

    if(index >= folder.size()) {

        return nullptr;

    }

    if(folder[index].type() == typeid(std::string)) {

        return std::any_cast<std::string>(&folder[index]);

    } else if(folder[index].type() == typeid(std::pair<std::string, std::vector<std::any>>)) {

        return &std::any_cast<std::pair<std::string, std::vector<std::any>>>(&folder[index])->first;

    } else {

        return nullptr;

    }

}

std::string& PlaylistTree::Iterator::operator*() const {

    std::vector<std::any>& folder = todo_.top().first;
    const std::size_t& index = todo_.top().second;

    if(index >= folder.size()) {

        std::string temp;
        std::string& garbage_ref = temp;
        return garbage_ref;

    }

    if(folder[index].type() == typeid(std::string)) {

        return std::any_cast<std::string&>(folder[index]);

    } else if(folder[index].type() == typeid(std::pair<std::string, std::vector<std::any>>)) {

        return std::any_cast<std::pair<std::string, std::vector<std::any>>&>(folder[index]).first;

    } else {

        std::string temp;
        std::string& garbage_ref = temp;
        return garbage_ref;

    }

}

const std::string& PlaylistTree::ConstIterator::operator*() const {

    const std::vector<std::any>& folder = todo_.top().first;
    const std::size_t& index = todo_.top().second;

    if(index >= folder.size()) {

        std::string temp;
        std::string& garbage_ref = temp;
        return garbage_ref;

    }

    if(folder[index].type() == typeid(std::string)) {

        return std::any_cast<const std::string&>(folder[index]);

    } else if(folder[index].type() == typeid(std::pair<std::string, std::vector<std::any>>)) {

        return std::any_cast<const std::pair<std::string, std::vector<std::any>>&>(folder[index]).first;

    } else {

        std::string temp;
        std::string& garbage_ref = temp;
        return garbage_ref;

    }

}

bool PlaylistTree::Iterator::is_folder_start() const {

    std::vector<std::any>& folder = todo_.top().first;
    const std::size_t& index = todo_.top().second;

    if(index >= folder.size()) {

        return false;

    }

    return folder[index].type() == typeid(std::pair<std::string, std::vector<std::any>>);

}

bool PlaylistTree::ConstIterator::is_folder_start() const {

    const std::vector<std::any>& folder = todo_.top().first;
    const std::size_t& index = todo_.top().second;

    if(index >= folder.size()) {

        return false;

    }

    return folder[index].type() == typeid(std::pair<std::string, std::vector<std::any>>);

}

bool PlaylistTree::Iterator::is_folder_end() const {

    std::vector<std::any>& folder = todo_.top().first;
    const std::size_t& index = todo_.top().second;

    return index >= folder.size();

}

bool PlaylistTree::ConstIterator::is_folder_end() const {

    const std::vector<std::any>& folder = todo_.top().first;
    const std::size_t& index = todo_.top().second;

    return index >= folder.size();

}

bool PlaylistTree::Iterator::operator==(const Iterator& other) const {

    //Check that the iterators are in the same folder (via. their memory addresses) and are at the same index
    return (
        &todo_.top().first == &other.todo_.top().first && 
        todo_.top().second == other.todo_.top().second
    );

}

bool PlaylistTree::ConstIterator::operator==(const ConstIterator& other) const {

    //Check that the iterators are in the same folder (via. their memory addresses) and are at the same index
    return (
        &todo_.top().first == &other.todo_.top().first && 
        todo_.top().second == other.todo_.top().second
    );

}

bool PlaylistTree::Iterator::operator!=(const Iterator& other) const {

    return !(*this == other);

}

bool PlaylistTree::ConstIterator::operator!=(const ConstIterator& other) const {

    return !(*this == other);

}

PlaylistTree::Iterator PlaylistTree::begin() {

    return Iterator(root_, 0);

}

PlaylistTree::Iterator PlaylistTree::end() {

    return Iterator(root_, root_.size());

}

PlaylistTree::ConstIterator PlaylistTree::begin() const {

    return ConstIterator(root_, 0);

}

PlaylistTree::ConstIterator PlaylistTree::end() const {

    return ConstIterator(root_, root_.size());

}

PlaylistTree::ConstIterator PlaylistTree::cbegin() {

    return ConstIterator(root_, 0);

}

PlaylistTree::ConstIterator PlaylistTree::cend() {

    return ConstIterator(root_, root_.size());

}

std::vector<std::any>* PlaylistTree::find_folder(const std::vector<std::string>& folder_hierarchy) {

    std::vector<std::any>* folder = &root_;
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

const std::vector<std::any>& PlaylistTree::root() const {

    return root_;

}

std::string PlaylistTree::string() const {

    std::string str {"[root]:\n"};
    int num_indents {0};
    int indent_width {4};
    //The stack stores playlist folders-- more specifically, pairs of the folder itself and its iterator.
    std::stack<std::pair<const std::vector<std::any>&, std::vector<std::any>::const_iterator>> todo;
    todo.push({root_, root_.begin()});
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
