#ifndef PLAYLIST_TREE_HPP
#define PLAYLIST_TREE_HPP

#include <string>
#include <vector>
#include <any>
#include <stack>

//=====================================================================================================================================================
// A tree structure that stores playlist IDs. Each node represents a folder, and each node contains an ordered list of playlist IDs and other folders.
//=====================================================================================================================================================

namespace staccato {

    class PlaylistTree {

        private:
        
        //A vector of strings and vectors. A string represents a playlist ID, and a vector represents a folder of more playlist IDs and folders.
        //Folders are represented by pair<string, vector<any>>, with pair.first being the folder name and pair.second being its contents.
        std::vector<std::any> root;

        std::vector<std::any>* find_folder(const std::vector<std::string>& folder_hierarchy);

        std::vector<std::string> get_folder_playlists_recursive(const std::vector<std::any>& folder) const;

        public:

        PlaylistTree();
        PlaylistTree(const PlaylistTree& other) = delete;
        PlaylistTree(PlaylistTree&& other) = delete;
        PlaylistTree& operator=(const PlaylistTree& other) = delete;
        PlaylistTree& operator=(PlaylistTree&& other) = delete;

        /// @brief Used to add a playlist to a folder specified by `folder_hierarchy` (or to the root if `folder_hierarchy` is empty)
        /// @param id 
        /// @param folder_hierarchy List of folders in hierarchical order (top to bottom)
        /// @return `true` if the folder exists, `false` otherwise
        bool add_playlist(std::string id, const std::vector<std::string>& folder_hierarchy);

        /// @brief Used to add an empty folder to a parent folder specified by `folder_hierarchy` (or to the root if `folder_hierarchy` is empty)
        /// @param name 
        /// @param folder_hierarchy List of folders in hierarchical order (top to bottom)
        bool add_folder(std::string name, const std::vector<std::string>& folder_hierarchy);

        /// @brief Used to remove a playlist from a folder specified by `folder_hierarchy` (or from the root if `folder_hierarchy` is empty)
        /// @param id 
        /// @param folder_hierarchy List of folders in hierarchical order (top to bottom)
        bool remove_playlist(std::string id, const std::vector<std::string>& folder_hierarchy);

        /// @brief Used to recursively remove a folder from a parent folder specified by `folder_hierarchy` (or from the root if `folder_hierarchy` is empty)
        /// @param name 
        /// @param folder_hierarchy
        /// @return The playlists included in the removed folder
        std::vector<std::string> remove_folder(std::string name, const std::vector<std::string>& folder_hierarchy);

        /// @return A string representation of this tree object
        std::string string() const;

    };

}

#endif